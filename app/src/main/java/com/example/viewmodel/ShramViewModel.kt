package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookingEntity
import com.example.data.CooperativeVoteEntity
import com.example.data.ShramDatabase
import com.example.data.ShramRepository
import com.example.data.WorkerEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RateCardItem(
    val trade: String,
    val serviceName: String,
    val cooperativeStandardRate: Int,
    val typicalMarketRate: Int,
    val unit: String,
    val description: String
)

class ShramViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShramRepository

    // Landing Page State
    val showLandingPage = MutableStateFlow(true)

    // User perspective: "CUSTOMER" or "WORKER"
    val userRole = MutableStateFlow("CUSTOMER")

    // Authentication & Profile State
    val isLoggedIn = MutableStateFlow(false)
    val showLoginScreen = MutableStateFlow(true)
    val currentUserName = MutableStateFlow("Ananya Sen")
    val currentUserPhone = MutableStateFlow("+91 98765 43210")

    // Dedicated Page Navigation
    // Customer Pages: "SERVICES", "EMERGENCY", "BOOKINGS", "RATE_CARD", "PROFILE"
    val currentCustomerPage = MutableStateFlow("SERVICES")

    // Worker Pages: "JOBS", "CALENDAR", "WALLET", "COOP_HUB", "PROFILE"
    val currentWorkerPage = MutableStateFlow("JOBS")

    // Worker Availability Calendar State (e.g. "2026-09-12" -> Status)
    val workerSchedules = MutableStateFlow<Map<String, com.example.ui.screens.WorkerScheduleStatus>>(emptyMap())

    // Active Tab in Customer View: "MARKETPLACE", "BOOKINGS", "COOPERATIVE_IMPACT"
    val customerTab = MutableStateFlow("MARKETPLACE")

    // Active Tab in Worker View: "JOB_REQUESTS", "DIVIDEND_WALLET", "COOP_VOTING", "ID_CARD"
    val workerTab = MutableStateFlow("JOB_REQUESTS")

    // Filter and search
    val selectedTrade = MutableStateFlow("ALL")
    val searchQuery = MutableStateFlow("")
    val selectedLocation = MutableStateFlow("Connaught Place, New Delhi")

    // Selected items for modal/dialog
    val selectedWorkerForDetails = MutableStateFlow<WorkerEntity?>(null)
    val selectedWorkerForBooking = MutableStateFlow<WorkerEntity?>(null)
    val showBookingFlow = MutableStateFlow(false)
    val bookingFlowTrade = MutableStateFlow<String?>("Electrician")
    val showRateCardDialog = MutableStateFlow(false)
    val showCooperativeNotice = MutableStateFlow(false)
    val showGharSathiChat = MutableStateFlow(false)

    // Current demo worker logged in (Rameshwar Sharma, id = 1)
    val currentWorkerId = MutableStateFlow(1L)

    val allWorkers: StateFlow<List<WorkerEntity>>
    val allBookings: StateFlow<List<BookingEntity>>
    val allVotes: StateFlow<List<CooperativeVoteEntity>>

    val filteredWorkers: StateFlow<List<WorkerEntity>>

    init {
        val db = ShramDatabase.getDatabase(application)
        repository = ShramRepository(db.shramDao())

        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }

        allWorkers = repository.allWorkers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allBookings = repository.allBookings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allVotes = repository.allVotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        filteredWorkers = combine(allWorkers, selectedTrade, searchQuery) { workers, trade, query ->
            workers.filter { worker ->
                val matchesTrade = if (trade == "ALL") true else {
                    worker.trade.equals(trade, ignoreCase = true) ||
                    (trade.equals("Technician", ignoreCase = true) && worker.trade.contains("Tech", ignoreCase = true))
                }
                val matchesQuery = if (query.isBlank()) true else {
                    worker.name.contains(query, ignoreCase = true) ||
                    worker.trade.contains(query, ignoreCase = true) ||
                    worker.locality.contains(query, ignoreCase = true) ||
                    worker.specializations.contains(query, ignoreCase = true)
                }
                matchesTrade && matchesQuery
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun navigateToLanding() {
        showLandingPage.value = true
    }

    fun setCustomerPage(page: String) {
        currentCustomerPage.value = page
        showLandingPage.value = false
        showLoginScreen.value = false
    }

    fun setWorkerPage(page: String) {
        currentWorkerPage.value = page
        showLandingPage.value = false
        showLoginScreen.value = false
    }

    fun openLoginScreen() {
        showLoginScreen.value = true
    }

    fun closeLoginScreen() {
        showLoginScreen.value = false
    }

    fun loginUser(name: String, phone: String, role: String) {
        isLoggedIn.value = true
        currentUserName.value = name
        currentUserPhone.value = phone
        userRole.value = role
        showLoginScreen.value = false
        showLandingPage.value = false
        if (role == "WORKER") {
            currentWorkerPage.value = "JOBS"
        } else {
            currentCustomerPage.value = "SERVICES"
        }
    }

    fun logoutUser() {
        isLoggedIn.value = false
        showLoginScreen.value = true
    }

    fun updateWorkerSchedule(dateKey: String, status: com.example.ui.screens.WorkerScheduleStatus) {
        val updated = workerSchedules.value.toMutableMap()
        updated[dateKey] = status
        workerSchedules.value = updated
    }

    fun navigateToFindService(trade: String = "ALL") {
        userRole.value = "CUSTOMER"
        customerTab.value = "MARKETPLACE"
        currentCustomerPage.value = "SERVICES"
        selectedTrade.value = trade
        showLandingPage.value = false
    }

    fun navigateToFindServiceWithQuery(query: String, trade: String = "ALL") {
        userRole.value = "CUSTOMER"
        customerTab.value = "MARKETPLACE"
        currentCustomerPage.value = "SERVICES"
        selectedTrade.value = trade
        searchQuery.value = query
        showLandingPage.value = false
    }

    fun updateLocation(newLoc: String) {
        selectedLocation.value = newLoc
    }

    fun navigateToJoinWorker() {
        userRole.value = "WORKER"
        workerTab.value = "JOB_REQUESTS"
        currentWorkerPage.value = "JOBS"
        showLandingPage.value = false
    }

    fun switchRole(role: String) {
        userRole.value = role
        showLandingPage.value = false
        if (role == "WORKER") {
            currentWorkerPage.value = "JOBS"
        } else {
            currentCustomerPage.value = "SERVICES"
        }
    }

    fun setCustomerTab(tab: String) {
        customerTab.value = tab
        showLandingPage.value = false
    }

    fun setWorkerTab(tab: String) {
        workerTab.value = tab
        showLandingPage.value = false
    }

    fun filterByTrade(trade: String) {
        selectedTrade.value = trade
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun openWorkerDetails(worker: WorkerEntity?) {
        selectedWorkerForDetails.value = worker
    }

    fun openBookingDialog(worker: WorkerEntity?) {
        selectedWorkerForBooking.value = worker
    }

    fun openBookingFlow(trade: String? = null, worker: WorkerEntity? = null) {
        bookingFlowTrade.value = trade ?: worker?.trade ?: "Electrician"
        selectedWorkerForBooking.value = worker
        showBookingFlow.value = true
    }

    fun closeBookingFlow() {
        showBookingFlow.value = false
        selectedWorkerForBooking.value = null
    }

    fun toggleRateCard(show: Boolean) {
        showRateCardDialog.value = show
    }

    fun openGharSathiChat() {
        showGharSathiChat.value = true
    }

    fun closeGharSathiChat() {
        showGharSathiChat.value = false
    }

    fun createBooking(
        worker: WorkerEntity,
        serviceTitle: String,
        description: String,
        date: String,
        timeSlot: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        estimatedTotal: Int,
        paymentMethod: String
    ) {
        val welfare = (estimatedTotal * 0.03).toInt()
        val workerShare = estimatedTotal - welfare
        val randomOtp = (1000..9999).random().toString()

        val newBooking = BookingEntity(
            customerName = customerName.ifBlank { "You (Verified Customer)" },
            customerPhone = customerPhone.ifBlank { "+91 98765 43210" },
            customerAddress = customerAddress.ifBlank { "Sector 14, Delhi NCR" },
            workerId = worker.id,
            workerName = worker.name,
            trade = worker.trade,
            serviceTitle = serviceTitle,
            description = description,
            scheduledDate = date,
            scheduledTime = timeSlot,
            status = "CONFIRMED",
            totalAmount = estimatedTotal,
            welfareContribution = welfare,
            workerShare = workerShare,
            otpCode = randomOtp,
            paymentMethod = paymentMethod,
            isPaid = false
        )

        viewModelScope.launch {
            repository.createBooking(newBooking)
            selectedWorkerForBooking.value = null
            customerTab.value = "BOOKINGS"
        }
    }

    fun updateBookingStatus(bookingId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, newStatus)
        }
    }

    fun verifyOtpAndStartJob(booking: BookingEntity, inputOtp: String): Boolean {
        if (booking.otpCode.trim() == inputOtp.trim()) {
            viewModelScope.launch {
                repository.updateBookingStatus(booking.id, "SERVICE_STARTED")
            }
            return true
        }
        return false
    }

    fun completeAndRateJob(bookingId: Long, workerId: Long, amount: Int, rating: Float, review: String) {
        viewModelScope.launch {
            repository.completeBooking(bookingId, rating, review, workerId, amount)
        }
    }

    fun toggleAvailability(workerId: Long, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.setWorkerAvailability(workerId, isAvailable)
        }
    }

    fun voteOnResolution(vote: CooperativeVoteEntity, choice: String) {
        viewModelScope.launch {
            repository.castVote(vote.id, choice, vote.yesVotes, vote.noVotes)
        }
    }

    companion object {
        val standardRateCards = listOf(
            RateCardItem(
                trade = "Electrician",
                serviceName = "MCB / Switchboard Repair",
                cooperativeStandardRate = 250,
                typicalMarketRate = 450,
                unit = "per unit / inspection",
                description = "Inspection of tripping, earthing check, burnt switch replacement."
            ),
            RateCardItem(
                trade = "Electrician",
                serviceName = "Complete Room Rewiring",
                cooperativeStandardRate = 850,
                typicalMarketRate = 1400,
                unit = "per room",
                description = "Concealed conduit wire pulling with ISI mark cable testing."
            ),
            RateCardItem(
                trade = "Plumber",
                serviceName = "Tap & Mixer Leakage Repair",
                cooperativeStandardRate = 200,
                typicalMarketRate = 380,
                unit = "per fixture",
                description = "Spindle change, washer replacement, leak sealing."
            ),
            RateCardItem(
                trade = "Plumber",
                serviceName = "Overhead Water Tank Cleaning",
                cooperativeStandardRate = 600,
                typicalMarketRate = 1100,
                unit = "up to 1000L",
                description = "High pressure de-sludging, UV sterilization, antibac spray."
            ),
            RateCardItem(
                trade = "Carpenter",
                serviceName = "Door Lock & Handle Fitting",
                cooperativeStandardRate = 300,
                typicalMarketRate = 550,
                unit = "per lockset",
                description = "Mortise / cylindrical lock fitting with smooth alignment."
            ),
            RateCardItem(
                trade = "Carpenter",
                serviceName = "Modular Cabinet Hinge Overhaul",
                cooperativeStandardRate = 350,
                typicalMarketRate = 600,
                unit = "up to 4 doors",
                description = "Soft-close hydraulic hinge installation and gap alignment."
            ),
            RateCardItem(
                trade = "Painter",
                serviceName = "Waterproof Wall Patch & Touchup",
                cooperativeStandardRate = 450,
                typicalMarketRate = 800,
                unit = "up to 20 sq.ft",
                description = "Scraping, anti-fungal primer coat, wall putty, top finish."
            ),
            RateCardItem(
                trade = "Cleaner",
                serviceName = "Deep Kitchen & Washroom Scrubbing",
                cooperativeStandardRate = 499,
                typicalMarketRate = 899,
                unit = "per room pair",
                description = "Hospital-grade degreasing, tile descaling, sink & exhaust sanitation."
            ),
            RateCardItem(
                trade = "Caregiver",
                serviceName = "Assisted Daily Care & Vitals",
                cooperativeStandardRate = 399,
                typicalMarketRate = 750,
                unit = "4-hour shift",
                description = "Mobility support, BP/blood sugar vitals logging, medication reminders."
            ),
            RateCardItem(
                trade = "Driver",
                serviceName = "City Chauffeur On-Demand",
                cooperativeStandardRate = 249,
                typicalMarketRate = 450,
                unit = "up to 2 hours",
                description = "Manual / Automatic certified driver, zero surge, verified badge."
            ),
            RateCardItem(
                trade = "Gardener",
                serviceName = "Lawn Mowing & Pruning Overhaul",
                cooperativeStandardRate = 299,
                typicalMarketRate = 550,
                unit = "up to 500 sq.ft",
                description = "Weed clearing, organic compost treatment, shrub and hedge shaping."
            ),
            RateCardItem(
                trade = "Technician",
                serviceName = "Appliance Motor & PCB Diagnosis",
                cooperativeStandardRate = 350,
                typicalMarketRate = 650,
                unit = "inspection & diagnosis",
                description = "Capacitor check, PCB circuit test, belt tightening, motor alignment."
            ),
            RateCardItem(
                trade = "Technician",
                serviceName = "Deep Jet Foam AC Service",
                cooperativeStandardRate = 499,
                typicalMarketRate = 899,
                unit = "per Split AC",
                description = "Indoor blower wash with jet pump jacket, antibacterial fin coil clean."
            )
        )
    }
}
