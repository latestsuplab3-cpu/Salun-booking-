package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SalonDatabase
import com.example.data.model.*
import com.example.data.remote.supabase.SupabaseClient
import com.example.data.remote.supabase.SupabaseConfig
import com.example.data.remote.supabase.SupabaseSyncManager
import com.example.data.repository.SalonRepository
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SalonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SalonRepository

    val standardSlots = listOf(
        "09:00 AM",
        "10:00 AM",
        "11:00 AM",
        "12:00 PM",
        "02:00 PM",
        "03:00 PM",
        "04:00 PM",
        "05:00 PM",
        "06:00 PM",
        "07:00 PM",
        "08:00 PM"
    )

    // Language state - Default is English as requested
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Active panel: "LOGIN_SELECTOR", "CUSTOMER", "BARBER", or "MASTER"
    private val _activePanel = MutableStateFlow("LOGIN_SELECTOR")
    val activePanel: StateFlow<String> = _activePanel.asStateFlow()

    // Current logged in user
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Current active salon license (if logged in as salon owner)
    private val _currentSalonLicense = MutableStateFlow<SalonLicenseEntity?>(null)
    val currentSalonLicense: StateFlow<SalonLicenseEntity?> = _currentSalonLicense.asStateFlow()

    // Services list from Room
    val services: StateFlow<List<ServiceItemEntity>>

    // All bookings from Room
    val allBookings: StateFlow<List<BookingEntity>>

    // All issued salon licenses (for App Creator & Salon authentication)
    val salonLicenses: StateFlow<List<SalonLicenseEntity>>

    // Reviews list
    val reviews: StateFlow<List<ReviewEntity>>

    // Notifications for current role
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    // Booking pre-order form state
    private val _selectedServices = MutableStateFlow<Set<ServiceItemEntity>>(emptySet())
    val selectedServices: StateFlow<Set<ServiceItemEntity>> = _selectedServices.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDateString: String = dateFormat.format(Date())

    private val _selectedDate = MutableStateFlow(todayDateString)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedSlot = MutableStateFlow("")
    val selectedSlot: StateFlow<String> = _selectedSlot.asStateFlow()

    // User message snackbar / toast state
    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Supabase Cloud Sync State
    private val supabaseSyncManager: SupabaseSyncManager
    private val _isSyncingSupabase = MutableStateFlow(false)
    val isSyncingSupabase: StateFlow<Boolean> = _isSyncingSupabase.asStateFlow()

    private val _lastSupabaseSyncStatus = MutableStateFlow<String?>("Connected (Project: llhyjuqthwsdmauvucqc)")
    val lastSupabaseSyncStatus: StateFlow<String?> = _lastSupabaseSyncStatus.asStateFlow()

    init {
        val database = SalonDatabase.getDatabase(application)
        repository = SalonRepository(database)
        supabaseSyncManager = SupabaseSyncManager(database)

        services = repository.allServices.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allBookings = repository.allBookings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        reviews = repository.allReviews.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        salonLicenses = repository.allSalonLicenses.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }

        // Collect notifications reactively based on active panel
        viewModelScope.launch {
            _activePanel.collectLatest { panel ->
                val role = if (panel == "BARBER") "BARBER" else "CUSTOMER"
                launch {
                    repository.getNotificationsForRole(role).collect { list ->
                        _notifications.value = list
                    }
                }
                launch {
                    repository.getUnreadNotificationCount(role).collect { count ->
                        _unreadCount.value = count
                    }
                }
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun setPanel(panel: String) {
        _activePanel.value = panel
    }

    fun clearFeedback() {
        _userFeedbackMessage.value = null
    }

    fun loginOrRegisterCustomer(
        name: String,
        phone: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val trimmedPhone = phone.trim()
            val trimmedPass = pass.trim()
            val trimmedName = name.trim()
            val lang = _currentLanguage.value

            if (trimmedName.isBlank()) {
                onError(when (lang) {
                    AppLanguage.ENGLISH -> "Please enter your full name"
                    AppLanguage.HINDI -> "कृपया अपना पूरा नाम दर्ज करें"
                    else -> "দয়া করে আপনার সম্পূর্ণ নাম লিখুন"
                })
                return@launch
            }
            if (trimmedPhone.length < 8) {
                onError(when (lang) {
                    AppLanguage.ENGLISH -> "Please enter a valid phone number"
                    AppLanguage.HINDI -> "कृपया मान्य फोन नंबर दर्ज करें"
                    else -> "দয়া করে সঠিক মোবাইল নম্বর লিখুন"
                })
                return@launch
            }
            if (trimmedPass.length < 3) {
                onError(when (lang) {
                    AppLanguage.ENGLISH -> "Password must be at least 3 characters"
                    AppLanguage.HINDI -> "पासवर्ड कम से कम 3 अक्षरों का होना चाहिए"
                    else -> "পাসওয়ার্ড অন্তত ৩ অক্ষরের হতে হবে"
                })
                return@launch
            }

            val existingUser = repository.getUserByPhone(trimmedPhone)
            if (existingUser != null) {
                if (existingUser.password == trimmedPass) {
                    val updatedUser = if (existingUser.name != trimmedName) {
                        val updated = existingUser.copy(name = trimmedName)
                        repository.registerUser(updated)
                        updated
                    } else {
                        existingUser
                    }
                    _currentUser.value = updatedUser
                    _activePanel.value = "CUSTOMER"
                    _userFeedbackMessage.value = when (lang) {
                        AppLanguage.ENGLISH -> "Welcome, ${updatedUser.name}! Continue pre-order."
                        AppLanguage.HINDI -> "स्वागत है, ${updatedUser.name}! प्री-ऑर्डर जारी रखें।"
                        else -> "স্বাগতম, ${updatedUser.name}! প্রি-অর্ডার চালিয়ে যান।"
                    }
                    onSuccess()
                } else {
                    onError(when (lang) {
                        AppLanguage.ENGLISH -> "Incorrect password! Please try again."
                        AppLanguage.HINDI -> "गलत पासवर्ड! कृपया पुनः प्रयास करें।"
                        else -> "ভুল পাসওয়ার্ড! সঠিক পাসওয়ার্ড দিন।"
                    })
                }
            } else {
                val newUser = UserEntity(
                    phone = trimmedPhone,
                    name = trimmedName,
                    password = trimmedPass,
                    role = "CUSTOMER"
                )
                repository.registerUser(newUser)
                val created = repository.getUserByPhone(trimmedPhone) ?: newUser
                _currentUser.value = created
                _activePanel.value = "CUSTOMER"
                _userFeedbackMessage.value = when (lang) {
                    AppLanguage.ENGLISH -> "Registration successful! Welcome, ${created.name}!"
                    AppLanguage.HINDI -> "पंजीकरण सफल! स्वागत है, ${created.name}!"
                    else -> "রেজিস্ট্রেশন সফল! স্বাগতম, ${created.name}!"
                }
                onSuccess()
            }
        }
    }

    fun loginWithPhonePassword(
        phone: String,
        pass: String,
        expectedRole: String,
        name: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val trimmedPhone = phone.trim()
            val trimmedPass = pass.trim()
            val trimmedName = name.trim()

            val user = repository.getUserByPhone(trimmedPhone)
            if (user != null) {
                if (user.password == trimmedPass) {
                    val updatedUser = if (trimmedName.isNotBlank() && user.name != trimmedName) {
                        val updated = user.copy(name = trimmedName)
                        repository.registerUser(updated)
                        updated
                    } else {
                        user
                    }
                    _currentUser.value = updatedUser
                    _activePanel.value = updatedUser.role
                    onSuccess()
                } else {
                    onError("ভুল পাসওয়ার্ড! সঠিক পাসওয়ার্ড দিন।")
                }
            } else {
                // Auto create account for customer ease
                val displayName = if (trimmedName.isNotBlank()) trimmedName
                else if (expectedRole == "BARBER") "সেলুন মাস্টার ($trimmedPhone)"
                else "কাস্টমার ($trimmedPhone)"

                val newUser = UserEntity(
                    phone = trimmedPhone,
                    name = displayName,
                    password = trimmedPass,
                    role = expectedRole
                )
                repository.registerUser(newUser)
                val created = repository.getUserByPhone(trimmedPhone) ?: newUser
                _currentUser.value = created
                _activePanel.value = expectedRole
                onSuccess()
            }
        }
    }

    fun loginWithFirebaseOtpSuccess(
        phone: String,
        expectedRole: String,
        name: String = "",
        salonId: String? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val trimmedPhone = phone.trim()
            val user = repository.getUserByPhone(trimmedPhone)
            if (user != null) {
                val updatedUser = if (name.isNotBlank() && user.name != name.trim()) {
                    val u = user.copy(name = name.trim())
                    repository.registerUser(u)
                    u
                } else user
                _currentUser.value = updatedUser
                _activePanel.value = if (expectedRole.isNotBlank()) expectedRole else user.role
                if (expectedRole == "BARBER" && !salonId.isNullOrBlank()) {
                    val license = repository.getSalonLicenseById(salonId)
                    _currentSalonLicense.value = license
                }
                onSuccess()
            } else {
                val displayName = if (name.isNotBlank()) name.trim()
                else if (expectedRole == "BARBER") "সেলুন ওনার ($trimmedPhone)"
                else if (expectedRole == "MASTER") "অ্যাপ ক্রিয়েটর ($trimmedPhone)"
                else "কাস্টমার ($trimmedPhone)"

                val newUser = UserEntity(
                    phone = trimmedPhone,
                    name = displayName,
                    password = "firebase_otp_verified",
                    role = expectedRole
                )
                repository.registerUser(newUser)
                val created = repository.getUserByPhone(trimmedPhone) ?: newUser
                _currentUser.value = created
                _activePanel.value = expectedRole
                if (expectedRole == "BARBER" && !salonId.isNullOrBlank()) {
                    val license = repository.getSalonLicenseById(salonId)
                    _currentSalonLicense.value = license
                }
                onSuccess()
            }
        }
    }

    fun quickSwitchUser(role: String) {
        viewModelScope.launch {
            when (role) {
                "BARBER" -> {
                    val user = repository.getUserByPhone("01700000000")
                    if (user != null) {
                        _currentUser.value = user
                        _activePanel.value = "BARBER"
                        val defaultLicense = repository.getSalonLicenseById("SALON-101")
                        _currentSalonLicense.value = defaultLicense
                    }
                }
                "CUSTOMER" -> {
                    val user = repository.getUserByPhone("01800000000")
                    if (user != null) {
                        _currentUser.value = user
                        _activePanel.value = "CUSTOMER"
                        _currentSalonLicense.value = null
                    }
                }
                "MASTER" -> {
                    val masterUser = UserEntity(
                        phone = "01999999999",
                        name = "App Creator (Master Admin)",
                        password = "admin",
                        role = "MASTER"
                    )
                    _currentUser.value = masterUser
                    _currentSalonLicense.value = null
                    _activePanel.value = "MASTER"
                }
                else -> {
                    logoutToSelector()
                }
            }
        }
    }

    /**
     * Dedicated Salon Owner / Agent Login:
     * Requires the official Salon ID issued by the App Creator + Phone Number + Password.
     */
    fun loginSalonOwner(
        salonId: String,
        phone: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanId = salonId.trim().uppercase()
            val cleanPhone = phone.trim()
            val cleanPass = pass.trim()

            if (cleanId.isBlank() || cleanPhone.isBlank() || cleanPass.isBlank()) {
                onError("Please provide Salon ID, Phone Number and Password.")
                return@launch
            }

            val license = repository.getSalonLicenseById(cleanId)
            if (license == null) {
                onError("Invalid Salon ID ($cleanId)! Please obtain a valid Salon ID from the App Creator.")
                return@launch
            }

            if (!license.isActive) {
                onError("This Salon ID ($cleanId) has been deactivated by the App Creator.")
                return@launch
            }

            // Verify credentials against the issued license
            val normLicensePhone = license.ownerPhone.filter { it.isDigit() }
            val normInputPhone = cleanPhone.filter { it.isDigit() }
            val phoneMatches = normLicensePhone == normInputPhone ||
                (normLicensePhone.length >= 10 && normInputPhone.endsWith(normLicensePhone.takeLast(10))) ||
                (normInputPhone.length >= 10 && normLicensePhone.endsWith(normInputPhone.takeLast(10)))

            if (!phoneMatches) {
                onError("Phone number does not match the registered owner of $cleanId.")
                return@launch
            }

            if (license.password != cleanPass) {
                onError("Incorrect password for Salon ID $cleanId.")
                return@launch
            }

            // Authenticated successfully! Register or sync user
            val existingUser = repository.getUserByPhone(cleanPhone)
            val barberUser = if (existingUser != null) {
                val updated = existingUser.copy(name = license.ownerName, role = "BARBER")
                repository.registerUser(updated)
                updated
            } else {
                val newUser = UserEntity(
                    phone = cleanPhone,
                    name = license.ownerName,
                    password = cleanPass,
                    role = "BARBER"
                )
                repository.registerUser(newUser)
                repository.getUserByPhone(cleanPhone) ?: newUser
            }

            _currentUser.value = barberUser
            _currentSalonLicense.value = license
            _activePanel.value = "BARBER"
            _userFeedbackMessage.value = "Welcome ${license.salonName}! Logged in as Salon Owner."
            onSuccess()
        }
    }

    /**
     * App Creator / Master Admin: Issue a new unique Salon ID to a Salon Owner
     */
    fun issueSalonLicense(
        salonName: String,
        ownerName: String,
        phone: String,
        pass: String,
        address: String,
        customId: String? = null,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanName = salonName.trim()
            val cleanOwner = ownerName.trim()
            val cleanPhone = phone.trim()
            val cleanPass = pass.trim()
            val cleanAddress = address.trim()

            if (cleanName.isBlank() || cleanOwner.isBlank() || cleanPhone.isBlank() || cleanPass.isBlank()) {
                onError("Please fill in all fields (Salon Name, Owner Name, Phone, Password).")
                return@launch
            }

            val generatedId = if (!customId.isNullOrBlank()) {
                customId.trim().uppercase()
            } else {
                val randomSuffix = (1000..9999).random()
                "SALON-$randomSuffix"
            }

            val existing = repository.getSalonLicenseById(generatedId)
            if (existing != null) {
                onError("Salon ID $generatedId already exists. Please try again.")
                return@launch
            }

            val newLicense = SalonLicenseEntity(
                salonId = generatedId,
                salonName = cleanName,
                ownerName = cleanOwner,
                ownerPhone = cleanPhone,
                password = cleanPass,
                address = if (cleanAddress.isBlank()) "Main Road Branch" else cleanAddress,
                isActive = true,
                issuedBy = "App Creator"
            )

            repository.createSalonLicense(newLicense)
            _userFeedbackMessage.value = "Salon ID $generatedId issued successfully!"
            onSuccess(generatedId)
        }
    }

    fun toggleSalonLicenseStatus(salonId: String, currentActive: Boolean) {
        viewModelScope.launch {
            repository.updateSalonLicenseStatus(salonId, !currentActive)
            _userFeedbackMessage.value = "Salon status updated: ${if (!currentActive) "Active" else "Inactive"}"
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentSalonLicense.value = null
        _activePanel.value = "LOGIN_SELECTOR"
        _userFeedbackMessage.value = "Logged out successfully."
    }

    fun logoutSalonOwner() {
        _currentUser.value = null
        _currentSalonLicense.value = null
        _activePanel.value = "LOGIN_SELECTOR"
        _userFeedbackMessage.value = "Salon owner logged out."
    }

    fun logoutToSelector() {
        _currentUser.value = null
        _currentSalonLicense.value = null
        _activePanel.value = "LOGIN_SELECTOR"
        _userFeedbackMessage.value = "Logged out. Please choose your login portal."
    }

    /**
     * Master / App Creator Login:
     * Validates Master Admin ID & Secret Password for platform administrators.
     */
    fun loginMasterAdmin(
        adminId: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanId = adminId.trim().uppercase()
            val cleanPass = pass.trim()
            val validIds = listOf("MASTER-ADMIN", "MASTER", "ADMIN", "CREATOR", "CREATOR-01", "APP-CREATOR")
            val validPasses = listOf("admin123", "master123", "creator123", "123456", "admin", "123")

            if (cleanId.isBlank() || cleanPass.isBlank()) {
                onError("Please enter Master Admin ID and Secret Password.")
                return@launch
            }

            if (validIds.contains(cleanId) && validPasses.contains(cleanPass)) {
                val masterUser = UserEntity(
                    phone = "01999999999",
                    name = "App Creator & Master Admin",
                    password = cleanPass,
                    role = "MASTER"
                )
                _currentUser.value = masterUser
                _currentSalonLicense.value = null
                _activePanel.value = "MASTER"
                _userFeedbackMessage.value = "Welcome, App Creator!"
                onSuccess()
            } else {
                onError("Invalid Master ID or Password! Use MASTER-ADMIN / admin123")
            }
        }
    }

    fun updateCustomerProfile(name: String, onSuccess: () -> Unit) {
        val user = _currentUser.value ?: return
        if (name.isBlank()) {
            _userFeedbackMessage.value = "Name cannot be empty"
            return
        }
        viewModelScope.launch {
            val updated = user.copy(name = name.trim())
            repository.updateUser(updated)
            _currentUser.value = updated
            _userFeedbackMessage.value = "Profile updated successfully!"
            onSuccess()
        }
    }

    fun updateSalonOwnerProfile(
        salonId: String,
        newOwnerName: String,
        newSalonName: String,
        newPhone: String,
        newAddress: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val license = repository.getSalonLicenseById(salonId)
            val updatedLicense = (license ?: SalonLicenseEntity(
                salonId = salonId,
                salonName = newSalonName.trim(),
                ownerName = newOwnerName.trim(),
                ownerPhone = newPhone.trim(),
                password = "123",
                address = newAddress.trim()
            )).copy(
                salonName = newSalonName.trim(),
                ownerName = newOwnerName.trim(),
                ownerPhone = newPhone.trim(),
                address = newAddress.trim()
            )
            repository.createSalonLicense(updatedLicense)
            _currentSalonLicense.value = updatedLicense

            val user = _currentUser.value
            if (user != null && user.role == "BARBER") {
                val updatedUser = user.copy(
                    name = newOwnerName.trim(),
                    phone = newPhone.trim()
                )
                repository.updateUser(updatedUser)
                _currentUser.value = updatedUser
            }
            _userFeedbackMessage.value = "Salon owner profile updated successfully!"
            onSuccess()
        }
    }

    fun toggleService(service: ServiceItemEntity) {
        val current = _selectedServices.value.toMutableSet()
        val existing = current.find { it.id == service.id }
        if (existing != null) {
            current.remove(existing)
        } else {
            current.add(service)
        }
        _selectedServices.value = current
    }

    fun clearSelectedServices() {
        _selectedServices.value = emptySet()
        _selectedSlot.value = ""
    }

    fun setDate(date: String) {
        _selectedDate.value = date
        _selectedSlot.value = ""
    }

    fun setSlot(slot: String) {
        _selectedSlot.value = slot
    }

    fun isSlotBooked(date: String, slot: String): Boolean {
        return allBookings.value.any { booking ->
            booking.bookingDate == date && booking.timeSlot == slot && booking.status != "CANCELLED"
        }
    }

    fun calculateBookingSummary(): Triple<Double, Double, Double> {
        val total = _selectedServices.value.sumOf { it.price }
        val advancePercent = 55.0 // 55% advance prepayment requirement
        val advance = Math.round((total * (advancePercent / 100.0)) * 100.0) / 100.0
        val remaining = Math.round((total - advance) * 100.0) / 100.0
        return Triple(total, advance, remaining)
    }

    fun submitPreOrderBooking(onSuccess: (Long) -> Unit, onError: (String) -> Unit) {
        val user = _currentUser.value
        val lang = _currentLanguage.value
        if (user == null) {
            onError(when (lang) {
                AppLanguage.ENGLISH -> "Please sign in with your phone number first."
                AppLanguage.HINDI -> "कृपया पहले फोन नंबर से लॉगिन करें।"
                else -> "দয়া করে আগে মোবাইল নম্বর দিয়ে লগইন করুন।"
            })
            return
        }
        val selected = _selectedServices.value
        if (selected.isEmpty()) {
            onError(when (lang) {
                AppLanguage.ENGLISH -> "Please select at least one grooming service."
                AppLanguage.HINDI -> "कृपया कम से कम एक सेवा चुनें।"
                else -> "দয়া করে অন্তত একটি সেবা নির্বাচন করুন।"
            })
            return
        }
        val slot = _selectedSlot.value
        if (slot.isEmpty()) {
            onError(when (lang) {
                AppLanguage.ENGLISH -> "Please select an available time slot."
                AppLanguage.HINDI -> "कृपया एक उपलब्ध समय स्लॉट चुनें।"
                else -> "দয়া করে ফাঁকা টাইম স্লট সিলেক্ট করুন।"
            })
            return
        }

        if (isSlotBooked(_selectedDate.value, slot)) {
            onError(when (lang) {
                AppLanguage.ENGLISH -> "Sorry, this time slot has just been booked. Please select another slot."
                AppLanguage.HINDI -> "क्षमा करें, यह स्लॉट अभी बुक हो गया है। कृपया दूसरा स्लॉट चुनें।"
                else -> "দুঃখিত, এই টাইম স্লটটি ইতিমধ্যে বুক হয়ে গেছে। অন্য সময় বেছে নিন।"
            })
            return
        }

        val (total, advance, remaining) = calculateBookingSummary()
        val serviceNames = selected.joinToString(", ") { svc ->
            when (lang) {
                AppLanguage.ENGLISH -> svc.nameEn
                AppLanguage.HINDI -> svc.nameHi
                else -> svc.nameBn
            }
        }
        val txnId = "UPI" + UUID.randomUUID().toString().take(8).uppercase()

        val newBooking = BookingEntity(
            customerPhone = user.phone,
            customerName = user.name,
            serviceNames = serviceNames,
            bookingDate = _selectedDate.value,
            timeSlot = slot,
            totalPrice = total,
            advancePercentage = 55,
            advancePaid = advance,
            remainingDue = remaining,
            status = "CONFIRMED", // Confirmed upon 55% advance payment
            paymentMethod = "UPI QR (55% Advance)",
            transactionId = txnId
        )

        viewModelScope.launch {
            val bookingId = repository.createBooking(newBooking)
            clearSelectedServices()
            _userFeedbackMessage.value = when (lang) {
                AppLanguage.ENGLISH -> "Booking confirmed! 55% Advance (₹$advance) received via UPI."
                AppLanguage.HINDI -> "बुकिंग की पुष्टि हुई! ₹$advance अग्रिम UPI द्वारा प्राप्त हुआ।"
                else -> "বুকিং নিশ্চিত হয়েছে! ৫৫% অগ্রিম (₹$advance) UPI দ্বারা গৃহীত হয়েছে।"
            }
            // Auto-sync booking to Supabase in background
            launch {
                try {
                    supabaseSyncManager.syncSingleBooking(newBooking.copy(id = bookingId.toInt()))
                } catch (_: Exception) {}
            }
            onSuccess(bookingId)
        }
    }

    fun updateBookingStatus(bookingId: Int, newStatus: String, customerPhone: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, newStatus, customerPhone)
            _userFeedbackMessage.value = "Booking status updated: $newStatus"
        }
    }

    fun addCustomerReview(
        bookingId: Int,
        rating: Int,
        comment: String,
        serviceName: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val review = ReviewEntity(
                bookingId = bookingId,
                customerName = user.name,
                customerPhone = user.phone,
                rating = rating,
                comment = comment.trim(),
                serviceName = serviceName
            )
            repository.submitReview(review)
            _userFeedbackMessage.value = "Review submitted successfully!"
            onSuccess()
        }
    }

    fun updateServicePrice(id: Int, newPrice: Double) {
        viewModelScope.launch {
            repository.updateServicePrice(id, newPrice)
            _userFeedbackMessage.value = "Service price updated: ₹$newPrice"
        }
    }

    fun markNotificationRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            val role = if (_activePanel.value == "BARBER") "BARBER" else "CUSTOMER"
            repository.markAllNotificationsAsRead(role)
        }
    }

    /**
     * Trigger immediate two-way/cloud sync to Supabase with user feedback
     */
    fun syncNowToSupabase(onComplete: ((Boolean, String) -> Unit)? = null) {
        if (_isSyncingSupabase.value) return
        viewModelScope.launch {
            _isSyncingSupabase.value = true
            _userFeedbackMessage.value = "Syncing with Supabase Cloud..."
            val result = supabaseSyncManager.syncAll()
            _isSyncingSupabase.value = false
            if (result.isSuccess) {
                val msg = result.getOrNull() ?: "Supabase synced successfully!"
                _lastSupabaseSyncStatus.value = "Synced successfully just now"
                _userFeedbackMessage.value = "Supabase Cloud Sync Successful!"
                onComplete?.invoke(true, msg)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Unknown sync error"
                _lastSupabaseSyncStatus.value = "Sync error: $errorMsg"
                _userFeedbackMessage.value = "Supabase Sync: $errorMsg"
                onComplete?.invoke(false, errorMsg)
            }
        }
    }
}
