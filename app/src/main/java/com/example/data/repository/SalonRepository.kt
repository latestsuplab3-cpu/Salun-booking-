package com.example.data.repository

import com.example.data.local.SalonDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SalonRepository(private val database: SalonDatabase) {

    val allServices: Flow<List<ServiceItemEntity>> = database.serviceDao().getAllServices()
    val allBookings: Flow<List<BookingEntity>> = database.bookingDao().getAllBookingsFlow()
    val allReviews: Flow<List<ReviewEntity>> = database.reviewDao().getAllReviewsFlow()
    val allSalonLicenses: Flow<List<SalonLicenseEntity>> = database.salonLicenseDao().getAllLicensesFlow()

    fun getCustomerBookings(phone: String): Flow<List<BookingEntity>> =
        database.bookingDao().getBookingsByCustomerFlow(phone)

    fun getNotificationsForRole(role: String): Flow<List<NotificationEntity>> =
        database.notificationDao().getNotificationsForRoleFlow(role)

    fun getUnreadNotificationCount(role: String): Flow<Int> =
        database.notificationDao().getUnreadCountFlow(role)

    suspend fun getBookingsForDate(date: String): List<BookingEntity> = withContext(Dispatchers.IO) {
        database.bookingDao().getBookingsForDate(date)
    }

    suspend fun getUserByPhone(phone: String): UserEntity? = withContext(Dispatchers.IO) {
        database.userDao().getUserByPhone(phone)
    }

    suspend fun registerUser(user: UserEntity): Long = withContext(Dispatchers.IO) {
        database.userDao().insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        database.userDao().updateUser(user)
    }

    suspend fun createBooking(booking: BookingEntity): Long = withContext(Dispatchers.IO) {
        val bookingId = database.bookingDao().insertBooking(booking)
        // Also automatically notify the Barber!
        val notifyTitle = "New Booking! #$bookingId"
        val notifyMsg = "${booking.customerName} (${booking.customerPhone}) booked ${booking.serviceNames} for ${booking.timeSlot} (${booking.bookingDate}). 55% Advance (₹${booking.advancePaid}) received."
        database.notificationDao().insertNotification(
            NotificationEntity(
                recipientRole = "BARBER",
                recipientPhone = "",
                title = notifyTitle,
                message = notifyMsg,
                bookingId = bookingId.toInt()
            )
        )
        bookingId
    }

    suspend fun updateBookingStatus(bookingId: Int, newStatus: String, customerPhone: String) = withContext(Dispatchers.IO) {
        database.bookingDao().updateBookingStatus(bookingId, newStatus)
        // Notify Customer about status change
        val title = when (newStatus) {
            "CONFIRMED" -> "Booking Confirmed! #$bookingId"
            "IN_PROGRESS" -> "Service In Progress! #$bookingId"
            "COMPLETED" -> "Service Completed! #$bookingId"
            "CANCELLED" -> "Booking Cancelled #$bookingId"
            else -> "Booking Update #$bookingId"
        }
        val message = "Your booking #$bookingId status: $newStatus. Thank you for choosing our salon."
        database.notificationDao().insertNotification(
            NotificationEntity(
                recipientRole = "CUSTOMER",
                recipientPhone = customerPhone,
                title = title,
                message = message,
                bookingId = bookingId
            )
        )
    }

    suspend fun submitReview(review: ReviewEntity) = withContext(Dispatchers.IO) {
        database.reviewDao().insertReview(review)
        database.bookingDao().markReviewed(review.bookingId)
        // Notify Barber about new review
        database.notificationDao().insertNotification(
            NotificationEntity(
                recipientRole = "BARBER",
                title = "New Customer Review! ★ ${review.rating}/5",
                message = "${review.customerName} reviewed: \"${review.comment}\"",
                bookingId = review.bookingId
            )
        )
    }

    suspend fun updateServicePrice(id: Int, newPrice: Double) = withContext(Dispatchers.IO) {
        database.serviceDao().updatePrice(id, newPrice)
    }

    suspend fun updateServiceAvailability(id: Int, isAvailable: Boolean) = withContext(Dispatchers.IO) {
        database.serviceDao().updateAvailability(id, isAvailable)
    }

    suspend fun addNewService(service: ServiceItemEntity) = withContext(Dispatchers.IO) {
        database.serviceDao().insertService(service)
    }

    suspend fun markNotificationAsRead(id: Int) = withContext(Dispatchers.IO) {
        database.notificationDao().markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead(role: String) = withContext(Dispatchers.IO) {
        database.notificationDao().markAllAsRead(role)
    }

    suspend fun getSalonLicenseById(salonId: String): SalonLicenseEntity? = withContext(Dispatchers.IO) {
        database.salonLicenseDao().getLicenseById(salonId.trim())
    }

    suspend fun createSalonLicense(license: SalonLicenseEntity): Long = withContext(Dispatchers.IO) {
        database.salonLicenseDao().insertLicense(license)
    }

    suspend fun updateSalonLicenseStatus(salonId: String, isActive: Boolean) = withContext(Dispatchers.IO) {
        database.salonLicenseDao().updateStatus(salonId, isActive)
    }

    suspend fun deleteSalonLicense(salonId: String) = withContext(Dispatchers.IO) {
        database.salonLicenseDao().deleteLicense(salonId)
    }

    suspend fun initializeDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        val count = database.serviceDao().getCount()
        if (count == 0) {
            val initialServices = listOf(
                ServiceItemEntity(
                    nameEn = "Hair Cut",
                    nameBn = "চুল কাটা",
                    nameHi = "बाल कटवाना",
                    descEn = "Professional hair trimming, styling and hair wash",
                    descBn = "স্টাইলিশ হেয়ার কাট ও ফ্রেশ হেয়ার ওয়াশ",
                    descHi = "स्टाइलिश बाल कटाई और हेयर वॉश",
                    price = 100.0,
                    durationMinutes = 30,
                    category = "HAIR"
                ),
                ServiceItemEntity(
                    nameEn = "Beard Shave & Trim",
                    nameBn = "দাড়ি কাটা ও সেভ",
                    nameHi = "दाढ़ी शेव और ट्रिम",
                    descEn = "Clean razor shave, warm towel finish and aftershave balm",
                    descBn = "ক্লিন রেজর শেভ, গরম তোয়ালে ও কুল আফটারশেভ বাম",
                    descHi = "क्लीन रेज़र शेव और कूल आफ्टरशेव बाम",
                    price = 40.0,
                    durationMinutes = 20,
                    category = "BEARD"
                ),
                ServiceItemEntity(
                    nameEn = "Facial Glow & Cleanup",
                    nameBn = "ফেসিয়াল ও ফেস ক্লিনআপ",
                    nameHi = "फेशियल और फेस क्लीनअप",
                    descEn = "Deep skin cleanse, exfoliation, herbal pack and glow steam",
                    descBn = "ডিপ স্কিন ক্লিনসিং, হারবাল প্যাক ও গ্লো স্টিম মাসাজ",
                    descHi = "डीप स्किन क्लींजिंग, हर्बल पैक और ग्लो स्टीम",
                    price = 500.0,
                    durationMinutes = 45,
                    category = "FACIAL"
                ),
                ServiceItemEntity(
                    nameEn = "Herbal Hair Spa",
                    nameBn = "হারবাল হেয়ার স্পা",
                    nameHi = "हर्बल हेयर स्पा",
                    descEn = "Deep conditioning hair spa for silky and healthy hair",
                    descBn = "সিল্কি ও মজবুত চুলের জন্য পুষ্টিকর হেয়ার স্পা",
                    descHi = "सिल्की और मजबूत बालों के लिए हर्बल हेयर स्पा",
                    price = 350.0,
                    durationMinutes = 40,
                    category = "SPA"
                ),
                ServiceItemEntity(
                    nameEn = "Hair Color & Highlights",
                    nameBn = "হেয়ার কালার ও ডাই",
                    nameHi = "हेयर कलर और डाई",
                    descEn = "Ammonia-free premium hair color application",
                    descBn = "অ্যামোনিয়া-মুক্ত দীর্ঘস্থায়ী প্রিমিয়াম হেয়ার কালার",
                    descHi = "अमोनिया मुक्त प्रीमियम हेयर कलर",
                    price = 400.0,
                    durationMinutes = 45,
                    category = "HAIR"
                ),
                ServiceItemEntity(
                    nameEn = "Head & Neck Massage",
                    nameBn = "হেড ও নেক ম্যাসাজ",
                    nameHi = "हेड और नेक मसाज",
                    descEn = "Relaxing herbal oil massage for stress relief",
                    descBn = "রিলাক্সিং হারবাল তেল দিয়ে ক্লান্তি দূর করার হেড ম্যাসাজ",
                    descHi = "तनाव मुक्ति के लिए आरामदायक हर्बल ऑइल हेड मसाज",
                    price = 150.0,
                    durationMinutes = 25,
                    category = "SPA"
                ),
                ServiceItemEntity(
                    nameEn = "Royal VIP Grooming Combo",
                    nameBn = "রয়্যাল ভিআইপি কম্বো (চুল + দাড়ি + ফেসিয়াল)",
                    nameHi = "रॉयल वीआईपी कॉम्बो (बाल + दाढ़ी + फेशियल)",
                    descEn = "Complete package: Hair Cut + Beard Style + Glow Facial",
                    descBn = "সম্পূর্ণ প্যাকেজ: চুল কাটা + দাড়ি শেভ + গোল্ডেন ফেসিয়াল",
                    descHi = "सम्पूर्ण पैकेज: बाल कटाई + दाढ़ी स्टाइल + ग्लो फेशियल",
                    price = 600.0,
                    durationMinutes = 75,
                    category = "COMBO"
                )
            )
            database.serviceDao().insertAll(initialServices)

            // Seed default users for quick testing & immediate login
            database.userDao().insertUser(
                UserEntity(
                    phone = "9876543210",
                    name = "Rajesh Sharma (Salon Master)",
                    password = "123",
                    role = "BARBER"
                )
            )
            database.userDao().insertUser(
                UserEntity(
                    phone = "9800000000",
                    name = "Rahul Sharma (Customer)",
                    password = "123",
                    role = "CUSTOMER"
                )
            )

            // Sample initial review
            database.reviewDao().insertReview(
                ReviewEntity(
                    bookingId = 101,
                    customerName = "Vikram Malhotra",
                    customerPhone = "9811223344",
                    rating = 5,
                    comment = "Haircut & beard styling was outstanding! Very fast and attentive service.",
                    serviceName = "Haircut & Beard Styling"
                )
            )
            database.reviewDao().insertReview(
                ReviewEntity(
                    bookingId = 102,
                    customerName = "Rahul Sharma",
                    customerPhone = "9800000000",
                    rating = 5,
                    comment = "Best facial treatment experience. Clean ambience and polite barber!",
                    serviceName = "Facial Glow & Cleanup"
                )
            )

            // Sample initial notification
            database.notificationDao().insertNotification(
                NotificationEntity(
                    recipientRole = "BARBER",
                    title = "Welcome to Salon Panel!",
                    message = "Your salon management system is ready. Live booking notifications will appear here."
                )
            )
        }

        // Ensure Indian test users are always present
        if (database.userDao().getUserByPhone("9876543210") == null) {
            database.userDao().insertUser(
                UserEntity(
                    phone = "9876543210",
                    name = "Rajesh Sharma (Salon Master)",
                    password = "123",
                    role = "BARBER"
                )
            )
        }
        if (database.userDao().getUserByPhone("9800000000") == null) {
            database.userDao().insertUser(
                UserEntity(
                    phone = "9800000000",
                    name = "Rahul Sharma (Customer)",
                    password = "123",
                    role = "CUSTOMER"
                )
            )
        }

        // Seed default authorized salon licenses if empty
        val licenseCount = database.salonLicenseDao().getCount()
        if (licenseCount == 0) {
            database.salonLicenseDao().insertLicense(
                SalonLicenseEntity(
                    salonId = "SALON-101",
                    salonName = "Style Master Salon",
                    ownerName = "Rajesh Sharma (Owner)",
                    ownerPhone = "9876543210",
                    password = "123",
                    address = "Shop #12, City Center Mall",
                    isActive = true
                )
            )
            database.salonLicenseDao().insertLicense(
                SalonLicenseEntity(
                    salonId = "SALON-202",
                    salonName = "Royal Grooming Studio",
                    ownerName = "Rajesh Kumar (Owner)",
                    ownerPhone = "9876543211",
                    password = "pass",
                    address = "Market Square, Sector 4",
                    isActive = true
                )
            )
        } else {
            // Update SALON-101 if it has old phone
            val lic101 = database.salonLicenseDao().getLicenseById("SALON-101")
            if (lic101 != null && lic101.ownerPhone.startsWith("01")) {
                database.salonLicenseDao().insertLicense(
                    lic101.copy(ownerPhone = "9876543210", ownerName = "Rajesh Sharma (Owner)")
                )
            }
        }
    }
}
