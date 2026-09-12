package com.example.data

import androidx.compose.ui.graphics.Color

enum class BookingTrackingStatus(
    val code: String,
    val title: String,
    val description: String,
    val stepNumber: Int
) {
    CONFIRMED(
        code = "CONFIRMED",
        title = "Confirmed",
        description = "Cooperative order registered & verified",
        stepNumber = 1
    ),
    WORKER_ASSIGNED(
        code = "WORKER_ASSIGNED",
        title = "Worker Assigned",
        description = "Verified artisan allocated to your service",
        stepNumber = 2
    ),
    WORKER_ARRIVING(
        code = "WORKER_ARRIVING",
        title = "Worker Arriving",
        description = "Artisan is traveling to your location",
        stepNumber = 3
    ),
    SERVICE_STARTED(
        code = "SERVICE_STARTED",
        title = "Service Started",
        description = "OTP validated; service underway on site",
        stepNumber = 4
    ),
    COMPLETED(
        code = "COMPLETED",
        title = "Completed",
        description = "Work completed, inspected & settled",
        stepNumber = 5
    );

    fun nextStatus(): BookingTrackingStatus? {
        return when (this) {
            CONFIRMED -> WORKER_ASSIGNED
            WORKER_ASSIGNED -> WORKER_ARRIVING
            WORKER_ARRIVING -> SERVICE_STARTED
            SERVICE_STARTED -> COMPLETED
            COMPLETED -> null
        }
    }

    companion object {
        fun from(raw: String): BookingTrackingStatus {
            return when (raw.trim().uppercase()) {
                "CONFIRMED", "REQUESTED" -> CONFIRMED
                "WORKER_ASSIGNED", "ACCEPTED" -> WORKER_ASSIGNED
                "WORKER_ARRIVING", "EN_ROUTE" -> WORKER_ARRIVING
                "SERVICE_STARTED", "IN_PROGRESS" -> SERVICE_STARTED
                "COMPLETED" -> COMPLETED
                else -> CONFIRMED
            }
        }

        val orderedSteps = listOf(
            CONFIRMED,
            WORKER_ASSIGNED,
            WORKER_ARRIVING,
            SERVICE_STARTED,
            COMPLETED
        )
    }
}
