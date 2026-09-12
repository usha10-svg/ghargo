package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShramDao {

    // Workers
    @Query("SELECT * FROM workers ORDER BY rating DESC, completedJobs DESC")
    fun getAllWorkers(): Flow<List<WorkerEntity>>

    @Query("SELECT * FROM workers WHERE trade = :trade ORDER BY rating DESC")
    fun getWorkersByTrade(trade: String): Flow<List<WorkerEntity>>

    @Query("SELECT * FROM workers WHERE id = :id LIMIT 1")
    fun getWorkerById(id: Long): Flow<WorkerEntity?>

    @Query("SELECT COUNT(*) FROM workers")
    suspend fun getWorkerCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkers(workers: List<WorkerEntity>)

    @Query("UPDATE workers SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateWorkerAvailability(id: Long, isAvailable: Boolean)

    @Query("UPDATE workers SET completedJobs = completedJobs + 1, dividendEarned = dividendEarned + :dividendIncrement WHERE id = :id")
    suspend fun incrementWorkerStats(id: Long, dividendIncrement: Int)

    // Bookings
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE workerId = :workerId ORDER BY id DESC")
    fun getBookingsForWorker(workerId: Long): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, status: String)

    @Query("UPDATE bookings SET status = 'COMPLETED', ratingGiven = :rating, reviewGiven = :review, isPaid = 1 WHERE id = :id")
    suspend fun completeBooking(id: Long, rating: Float, review: String)

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Long): Flow<BookingEntity?>

    // Cooperative Votes
    @Query("SELECT * FROM cooperative_votes ORDER BY id ASC")
    fun getAllVotes(): Flow<List<CooperativeVoteEntity>>

    @Query("SELECT COUNT(*) FROM cooperative_votes")
    suspend fun getVoteCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVotes(votes: List<CooperativeVoteEntity>)

    @Query("UPDATE cooperative_votes SET userVoted = :choice, yesVotes = :newYes, noVotes = :newNo WHERE id = :id")
    suspend fun recordVote(id: Long, choice: String, newYes: Int, newNo: Int)
}
