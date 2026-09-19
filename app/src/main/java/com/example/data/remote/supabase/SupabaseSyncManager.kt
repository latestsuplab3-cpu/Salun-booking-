package com.example.data.remote.supabase

import android.util.Log
import com.example.data.local.SalonDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class SupabaseSyncManager(
    private val database: SalonDatabase,
    private val supabaseClient: SupabaseClient = SupabaseClient()
) {

    suspend fun syncAll(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val syncLicenses = syncSalonLicenses()
            val syncServices = syncServices()
            val syncBookings = syncBookings()
            val syncReviews = syncReviews()

            val summary = "Sync complete: Licenses(${syncLicenses.getOrDefault(0)}), Services(${syncServices.getOrDefault(0)}), Bookings(${syncBookings.getOrDefault(0)}), Reviews(${syncReviews.getOrDefault(0)})"
            Log.d("SupabaseSyncManager", summary)
            Result.success(summary)
        } catch (e: Exception) {
            Log.e("SupabaseSyncManager", "Failed full sync", e)
            Result.failure(e)
        }
    }

    suspend fun syncSalonLicenses(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val localLicenses = database.salonLicenseDao().getAllLicensesFlow().first()
            var count = 0
            for (lic in localLicenses) {
                val json = JSONObject().apply {
                    put("salon_id", lic.salonId)
                    put("salon_name", lic.salonName)
                    put("owner_name", lic.ownerName)
                    put("owner_phone", lic.ownerPhone)
                    put("password", lic.password)
                    put("address", lic.address)
                    put("is_active", lic.isActive)
                    put("issued_by", lic.issuedBy)
                    put("issued_at", lic.issuedAt)
                }
                supabaseClient.upsert("salon_licenses", json.toString(), onConflict = "salon_id")
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncServices(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val localServices = database.serviceDao().getAllServices().first()
            var count = 0
            for (srv in localServices) {
                val json = JSONObject().apply {
                    put("id", srv.id)
                    put("name_en", srv.nameEn)
                    put("name_bn", srv.nameBn)
                    put("name_hi", srv.nameHi)
                    put("desc_en", srv.descEn)
                    put("desc_bn", srv.descBn)
                    put("desc_hi", srv.descHi)
                    put("price", srv.price)
                    put("duration_minutes", srv.durationMinutes)
                    put("category", srv.category)
                    put("is_available", srv.isAvailable)
                }
                supabaseClient.upsert("services", json.toString(), onConflict = "id")
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncBookings(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val localBookings = database.bookingDao().getAllBookingsFlow().first()
            var count = 0
            for (b in localBookings) {
                val json = JSONObject().apply {
                    put("id", b.id)
                    put("customer_phone", b.customerPhone)
                    put("customer_name", b.customerName)
                    put("service_names", b.serviceNames)
                    put("booking_date", b.bookingDate)
                    put("time_slot", b.timeSlot)
                    put("total_price", b.totalPrice)
                    put("advance_percentage", b.advancePercentage)
                    put("advance_paid", b.advancePaid)
                    put("remaining_due", b.remainingDue)
                    put("status", b.status)
                    put("payment_method", b.paymentMethod)
                    put("transaction_id", b.transactionId)
                    put("created_at", b.createdAt)
                    put("is_reviewed", b.isReviewed)
                }
                supabaseClient.upsert("bookings", json.toString(), onConflict = "id")
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncSingleBooking(booking: BookingEntity): Result<String> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                if (booking.id > 0) put("id", booking.id)
                put("customer_phone", booking.customerPhone)
                put("customer_name", booking.customerName)
                put("service_names", booking.serviceNames)
                put("booking_date", booking.bookingDate)
                put("time_slot", booking.timeSlot)
                put("total_price", booking.totalPrice)
                put("advance_percentage", booking.advancePercentage)
                put("advance_paid", booking.advancePaid)
                put("remaining_due", booking.remainingDue)
                put("status", booking.status)
                put("payment_method", booking.paymentMethod)
                put("transaction_id", booking.transactionId)
                put("created_at", booking.createdAt)
                put("is_reviewed", booking.isReviewed)
            }
            supabaseClient.upsert("bookings", json.toString(), onConflict = "id")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncReviews(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val localReviews = database.reviewDao().getAllReviewsFlow().first()
            var count = 0
            for (r in localReviews) {
                val json = JSONObject().apply {
                    put("id", r.id)
                    put("booking_id", r.bookingId)
                    put("customer_name", r.customerName)
                    put("customer_phone", r.customerPhone)
                    put("rating", r.rating)
                    put("comment", r.comment)
                    put("service_name", r.serviceName)
                    put("created_at", r.createdAt)
                }
                supabaseClient.upsert("reviews", json.toString(), onConflict = "id")
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
