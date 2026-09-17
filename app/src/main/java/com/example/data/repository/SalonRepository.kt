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
        val notifyTitle = "নতুন বুকিং! #${bookingId}"
        val notifyMsg = "${booking.customerName} (${booking.customerPhone}) ${booking.serviceNames} এর জন্য ${booking.timeSlot} (${booking.bookingDate}) বুক করেছেন। অগ্রিম ৫৫% (₹${booking.advancePaid}) জমা হয়েছে।"
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
            "CONFIRMED" -> "বুকিং নিশ্চিত হয়েছে! (Confirmed)"
            "IN_PROGRESS" -> "আপনার সেবা চলছে! (In Progress)"
            "COMPLETED" -> "সেবা সম্পন্ন হয়েছে! (Completed)"
            "CANCELLED" -> "বুকিং বাতিল করা হয়েছে (Cancelled)"
            else -> "বুকিং আপডেট #$bookingId"
        }
        val message = "আপনার বুকিং #${bookingId} স্ট্যাটাস: $newStatus। সেলুনে সময়মতো আসার জন্য ধন্যবাদ।"
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
                title = "নতুন কাস্টমার রিভিউ! ★ ${review.rating}/5",
                message = "${review.customerName} লিখেছেন: \"${review.comment}\"",
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
                    phone = "01700000000",
                    name = "উস্তাদ নাপিত (Saloon Master)",
                    password = "123",
                    role = "BARBER"
                )
            )
            database.userDao().insertUser(
                UserEntity(
                    phone = "01800000000",
                    name = "সুমন ইসলাম (Customer)",
                    password = "123",
                    role = "CUSTOMER"
                )
            )

            // Sample initial review
            database.reviewDao().insertReview(
                ReviewEntity(
                    bookingId = 101,
                    customerName = "তানভীর আহমেদ",
                    customerPhone = "01711223344",
                    rating = 5,
                    comment = "চুল কাটা ও দাড়ি শেভ অসাধারণ হয়েছে! সার্ভিস খুব ফাস্ট ও যত্নবান।",
                    serviceName = "চুল কাটা ও দাড়ি শেভ"
                )
            )
            database.reviewDao().insertReview(
                ReviewEntity(
                    bookingId = 102,
                    customerName = "Rahul Sharma",
                    customerPhone = "09876543210",
                    rating = 5,
                    comment = "Best facial treatment experience. Clean ambience and polite barber!",
                    serviceName = "Facial Glow & Cleanup"
                )
            )

            // Sample initial notification
            database.notificationDao().insertNotification(
                NotificationEntity(
                    recipientRole = "BARBER",
                    title = "স্বাগতম সেলুন প্যানেলে!",
                    message = "আপনার সেলুন অ্যাপ চালু হয়েছে। এখানে নতুন বুকিং এর নোটিফিকেশন আসবে।"
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
                    ownerName = "Mohammad Ali (Owner)",
                    ownerPhone = "01700000000",
                    password = "123",
                    address = "Shop #12, City Center Mall",
                    isActive = true
                )
            )
            database.salonLicenseDao().insertLicense(
                SalonLicenseEntity(
                    salonId = "SALON-202",
                    salonName = "Royal Grooming Studio",
                    ownerName = "Rajesh Kumar",
                    ownerPhone = "9876543210",
                    password = "pass",
                    address = "Market Square, Sector 4",
                    isActive = true
                )
            )
        }
    }
}
