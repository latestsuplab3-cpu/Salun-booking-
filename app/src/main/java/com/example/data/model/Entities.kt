package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phone: String,
    val name: String,
    val password: String,
    val role: String, // "CUSTOMER" or "BARBER"
    val avatarColor: Long = 0xFFD97706,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "services")
data class ServiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameEn: String,
    val nameBn: String,
    val nameHi: String,
    val descEn: String,
    val descBn: String,
    val descHi: String,
    val price: Double,
    val durationMinutes: Int,
    val category: String, // "HAIR", "BEARD", "FACIAL", "COMBO", "SPA"
    val isAvailable: Boolean = true
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerPhone: String,
    val customerName: String,
    val serviceNames: String,
    val bookingDate: String, // e.g. "2026-09-16"
    val timeSlot: String,    // e.g. "10:00 AM"
    val totalPrice: Double,
    val advancePercentage: Int = 55,
    val advancePaid: Double,
    val remainingDue: Double,
    val status: String,      // "PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    val paymentMethod: String = "UPI Advance (55%)",
    val transactionId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isReviewed: Boolean = false
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val customerName: String,
    val customerPhone: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val serviceName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipientRole: String, // "BARBER" or "CUSTOMER"
    val recipientPhone: String = "",
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val bookingId: Int? = null
)

@Entity(tableName = "salon_licenses")
data class SalonLicenseEntity(
    @PrimaryKey val salonId: String, // e.g. "SALON-101", "SALON-5588"
    val salonName: String,           // e.g. "Style Master Salon"
    val ownerName: String,           // e.g. "Mohammad Ali"
    val ownerPhone: String,          // e.g. "01700000000"
    val password: String,            // e.g. "123"
    val address: String = "Main Market Branch",
    val isActive: Boolean = true,
    val issuedBy: String = "App Creator / Master Admin",
    val issuedAt: Long = System.currentTimeMillis()
)
