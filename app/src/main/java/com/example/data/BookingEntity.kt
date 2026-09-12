package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val workerId: Long,
    val workerName: String,
    val trade: String,
    val serviceTitle: String,
    val description: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val status: String, // CONFIRMED, WORKER_ASSIGNED, WORKER_ARRIVING, SERVICE_STARTED, COMPLETED
    val totalAmount: Int,
    val welfareContribution: Int,
    val workerShare: Int,
    val otpCode: String,
    val paymentMethod: String = "UPI / Jan Dhan",
    val isPaid: Boolean = false,
    val ratingGiven: Float = 0f,
    val reviewGiven: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
