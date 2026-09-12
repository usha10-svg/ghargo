package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WorkerEntity::class, BookingEntity::class, CooperativeVoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ShramDatabase : RoomDatabase() {
    abstract fun shramDao(): ShramDao

    companion object {
        @Volatile
        private var INSTANCE: ShramDatabase? = null

        fun getDatabase(context: Context): ShramDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShramDatabase::class.java,
                    "shram_connect.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
