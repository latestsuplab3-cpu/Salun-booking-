package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY id ASC")
    fun getAllServices(): Flow<List<ServiceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceItemEntity): Long

    @Update
    suspend fun updateService(service: ServiceItemEntity)

    @Query("UPDATE services SET price = :newPrice WHERE id = :id")
    suspend fun updatePrice(id: Int, newPrice: Double)

    @Query("UPDATE services SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateAvailability(id: Int, isAvailable: Boolean)

    @Query("SELECT COUNT(*) FROM services")
    suspend fun getCount(): Int
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookingsFlow(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE customerPhone = :phone ORDER BY id DESC")
    fun getBookingsByCustomerFlow(phone: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE bookingDate = :date")
    suspend fun getBookingsForDate(date: String): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE bookingDate = :date")
    fun getBookingsForDateFlow(date: String): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)

    @Query("UPDATE bookings SET isReviewed = 1 WHERE id = :id")
    suspend fun markReviewed(id: Int)

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: Int): BookingEntity?
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY id DESC")
    fun getAllReviewsFlow(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE recipientRole = :role ORDER BY id DESC")
    fun getNotificationsForRoleFlow(role: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientRole = :role")
    suspend fun markAllAsRead(role: String)

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientRole = :role AND isRead = 0")
    fun getUnreadCountFlow(role: String): Flow<Int>
}

@Dao
interface SalonLicenseDao {
    @Query("SELECT * FROM salon_licenses ORDER BY issuedAt DESC")
    fun getAllLicensesFlow(): Flow<List<SalonLicenseEntity>>

    @Query("SELECT * FROM salon_licenses WHERE salonId = :salonId LIMIT 1")
    suspend fun getLicenseById(salonId: String): SalonLicenseEntity?

    @Query("SELECT * FROM salon_licenses WHERE ownerPhone = :phone LIMIT 1")
    suspend fun getLicenseByPhone(phone: String): SalonLicenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLicense(license: SalonLicenseEntity): Long

    @Query("UPDATE salon_licenses SET isActive = :isActive WHERE salonId = :salonId")
    suspend fun updateStatus(salonId: String, isActive: Boolean)

    @Query("DELETE FROM salon_licenses WHERE salonId = :salonId")
    suspend fun deleteLicense(salonId: String)

    @Query("SELECT COUNT(*) FROM salon_licenses")
    suspend fun getCount(): Int
}
