package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cooperative_votes")
data class CooperativeVoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val resolutionNumber: String,
    val title: String,
    val description: String,
    val category: String, // "WAGE_POLICY", "WELFARE_FUND", "TOOL_SUBSIDY", "MEMBERSHIP"
    val yesVotes: Int,
    val noVotes: Int,
    val userVoted: String? = null, // "YES", "NO", or null
    val quorumReached: Boolean = false,
    val closesInDays: Int = 3
)
