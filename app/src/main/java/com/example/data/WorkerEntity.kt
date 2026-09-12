package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workers")
data class WorkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val trade: String, // Electrician, Plumber, Carpenter, Appliance Tech, Painter, Mason, Solar Tech
    val phone: String,
    val rating: Float,
    val reviewCount: Int,
    val hourlyRate: Int, // in INR (₹)
    val experienceYears: Int,
    val memberId: String, // e.g. SHRAM-DEL-0418
    val sharesOwned: Int, // Cooperative equity shares
    val dividendEarned: Int, // in INR
    val verifiedNsdc: Boolean, // Skill India / NSDC Certificate
    val verifiedEShram: Boolean, // e-Shram card linked
    val locality: String,
    val completedJobs: Int,
    val bio: String,
    val specializations: String, // comma separated skills
    val isAvailable: Boolean = true,
    val gender: String = "Male"
)
