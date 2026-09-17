package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        ServiceItemEntity::class,
        BookingEntity::class,
        ReviewEntity::class,
        NotificationEntity::class,
        SalonLicenseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SalonDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun bookingDao(): BookingDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao
    abstract fun salonLicenseDao(): SalonLicenseDao

    companion object {
        @Volatile
        private var INSTANCE: SalonDatabase? = null

        fun getDatabase(context: Context): SalonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalonDatabase::class.java,
                    "salon_booking_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
