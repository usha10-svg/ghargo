package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BookingBottomSheet
import com.example.ui.components.BookingFlowSheet
import com.example.ui.components.CooperativeTopAppBar
import com.example.ui.components.RateCardBottomSheet
import com.example.ui.components.WorkerDetailsBottomSheet
import com.example.ui.screens.CooperativeHubScreen
import com.example.ui.screens.CustomerBookingsScreen
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.screens.EmergencyServicesScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RateCardScreen
import com.example.ui.screens.WorkerCalendarScreen
import com.example.ui.screens.WorkerPortalScreen
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.ShramConnectTheme
import com.example.ui.theme.TextMuted
import com.example.viewmodel.ShramViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShramConnectTheme {
                ShramConnectApp()
            }
        }
    }
}

@Composable
fun ShramConnectApp(viewModel: ShramViewModel = viewModel()) {
    val showLandingPage by viewModel.showLandingPage.collectAsStateWithLifecycle()
    val showLoginScreen by viewModel.showLoginScreen.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUserName by viewModel.currentUserName.collectAsStateWithLifecycle()
    val currentUserPhone by viewModel.currentUserPhone.collectAsStateWithLifecycle()

    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val currentCustomerPage by viewModel.currentCustomerPage.collectAsStateWithLifecycle()
    val currentWorkerPage by viewModel.currentWorkerPage.collectAsStateWithLifecycle()
    val workerSchedules by viewModel.workerSchedules.collectAsStateWithLifecycle()

    val filteredWorkers by viewModel.filteredWorkers.collectAsStateWithLifecycle()
    val allWorkers by viewModel.allWorkers.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val allVotes by viewModel.allVotes.collectAsStateWithLifecycle()

    val selectedTrade by viewModel.selectedTrade.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val selectedWorkerForDetails by viewModel.selectedWorkerForDetails.collectAsStateWithLifecycle()
    val selectedWorkerForBooking by viewModel.selectedWorkerForBooking.collectAsStateWithLifecycle()
    val showBookingFlow by viewModel.showBookingFlow.collectAsStateWithLifecycle()
    val bookingFlowTrade by viewModel.bookingFlowTrade.collectAsStateWithLifecycle()
    val showRateCardDialog by viewModel.showRateCardDialog.collectAsStateWithLifecycle()

    // Demo artisan for worker perspective (Rameshwar Sharma)
    val currentWorker = allWorkers.firstOrNull { it.id == 1L } ?: allWorkers.firstOrNull()

    // If Login screen is active, display it as a full-screen dedicated page
    if (showLoginScreen) {
        LoginScreen(
            onLoginSuccess = { name, phone, role ->
                viewModel.loginUser(name, phone, role)
            },
            onContinueAsGuest = {
                viewModel.closeLoginScreen()
            },
            onBack = {
                viewModel.closeLoginScreen()
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CooperativeTopAppBar(
                currentRole = userRole,
                onRoleToggle = { role -> viewModel.switchRole(role) },
                onOpenRateCard = {
                    if (userRole == "CUSTOMER") {
                        viewModel.setCustomerPage("RATE_CARD")
                    } else {
                        viewModel.toggleRateCard(true)
                    }
                },
                isLanding = showLandingPage,
                onHomeClick = { viewModel.navigateToLanding() },
                onProfileClick = {
                    if (userRole == "CUSTOMER") {
                        viewModel.setCustomerPage("PROFILE")
                    } else {
                        viewModel.setWorkerPage("PROFILE")
                    }
                }
            )
        },
        bottomBar = {
            if (userRole == "CUSTOMER") {
                val activeBookingsCount = allBookings.count { it.status != "COMPLETED" && it.status != "CANCELLED" }

                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = showLandingPage,
                        onClick = { viewModel.navigateToLanding() },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = !showLandingPage && currentCustomerPage == "SERVICES",
                        onClick = { viewModel.setCustomerPage("SERVICES") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Services Marketplace"
                            )
                        },
                        label = { Text("Services", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_services")
                    )

                    NavigationBarItem(
                        selected = !showLandingPage && currentCustomerPage == "EMERGENCY",
                        onClick = { viewModel.setCustomerPage("EMERGENCY") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "24/7 Emergency"
                            )
                        },
                        label = { Text("Emergency", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFDC2626),
                            selectedTextColor = Color(0xFFDC2626),
                            indicatorColor = Color(0xFFFEF2F2),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_emergency")
                    )

                    NavigationBarItem(
                        selected = !showLandingPage && currentCustomerPage == "BOOKINGS",
                        onClick = { viewModel.setCustomerPage("BOOKINGS") },
                        icon = {
                            BadgedBox(badge = {
                                if (activeBookingsCount > 0) {
                                    Badge(containerColor = SaffronTrust) {
                                        Text("$activeBookingsCount")
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = "My Bookings"
                                )
                            }
                        },
                        label = { Text("Bookings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_bookings")
                    )

                    NavigationBarItem(
                        selected = !showLandingPage && currentCustomerPage == "PROFILE",
                        onClick = { viewModel.setCustomerPage("PROFILE") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "My Profile"
                            )
                        },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            } else {
                // Worker Bottom Navigation Bar
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = !showLandingPage && currentWorkerPage == "JOBS",
                        onClick = { viewModel.setWorkerPage("JOBS") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = "Active Jobs"
                            )
                        },
                        label = { Text("Jobs", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_worker_jobs")
                    )

                    NavigationBarItem(
                        selected = !showLandingPage && currentWorkerPage == "CALENDAR",
                        onClick = { viewModel.setWorkerPage("CALENDAR") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Worker Availability Calendar"
                            )
                        },
                        label = { Text("Calendar", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_worker_calendar")
                    )

                    NavigationBarItem(
                        selected = !showLandingPage && currentWorkerPage == "WALLET",
                        onClick = { viewModel.setWorkerPage("WALLET") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Coop Dividend Wallet"
                            )
                        },
                        label = { Text("Wallet", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_worker_wallet")
                    )

                    NavigationBarItem(
                        selected = !showLandingPage && currentWorkerPage == "COOP_HUB",
                        onClick = { viewModel.setWorkerPage("COOP_HUB") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.HowToVote,
                                contentDescription = "Democratic AGM"
                            )
                        },
                        label = { Text("AGM Vote", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_worker_vote")
                    )

                    NavigationBarItem(
                        selected = !showLandingPage && currentWorkerPage == "PROFILE",
                        onClick = { viewModel.setWorkerPage("PROFILE") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Worker Profile"
                            )
                        },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronTrust,
                            selectedTextColor = CooperativeNavy,
                            indicatorColor = SaffronTrust.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_worker_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showLandingPage) {
                LandingScreen(
                    onFindService = { viewModel.navigateToFindService() },
                    onJoinAsWorker = { viewModel.navigateToJoinWorker() },
                    onSelectTrade = { trade -> viewModel.navigateToFindService(trade) },
                    onOpenRateCard = { viewModel.setCustomerPage("RATE_CARD") },
                    onNavigateToEmergency = { viewModel.setCustomerPage("EMERGENCY") },
                    onSearchSubmit = { query -> viewModel.navigateToFindServiceWithQuery(query) },
                    currentLocation = selectedLocation,
                    onLocationChange = { loc -> viewModel.updateLocation(loc) },
                    onOpenLogin = { viewModel.openLoginScreen() }
                )
            } else if (userRole == "CUSTOMER") {
                when (currentCustomerPage) {
                    "SERVICES" -> {
                        CustomerHomeScreen(
                            workers = filteredWorkers,
                            allWorkers = allWorkers,
                            selectedTrade = selectedTrade,
                            searchQuery = searchQuery,
                            onSelectTrade = { trade -> viewModel.filterByTrade(trade) },
                            onSearchChange = { query -> viewModel.updateSearchQuery(query) },
                            onWorkerClick = { worker -> viewModel.openWorkerDetails(worker) },
                            onBookClick = { worker -> viewModel.openBookingFlow(worker.trade, worker) },
                            onStartBookingFlow = { trade, worker -> viewModel.openBookingFlow(trade, worker) },
                            onNavigateToEmergency = { viewModel.setCustomerPage("EMERGENCY") },
                            currentLocation = selectedLocation,
                            onLocationChange = { loc -> viewModel.updateLocation(loc) }
                        )
                    }
                    "EMERGENCY" -> {
                        EmergencyServicesScreen(
                            allWorkers = allWorkers,
                            onBookEmergencyWorker = { worker ->
                                viewModel.openBookingFlow(worker.trade, worker)
                            },
                            onBack = { viewModel.setCustomerPage("SERVICES") }
                        )
                    }
                    "BOOKINGS" -> {
                        CustomerBookingsScreen(
                            bookings = allBookings,
                            onAdvanceStatus = { id, status -> viewModel.updateBookingStatus(id, status) },
                            onCompleteAndRate = { bookingId, workerId, amount, rating, review ->
                                viewModel.completeAndRateJob(bookingId, workerId, amount, rating, review)
                            },
                            onExploreServices = { viewModel.setCustomerPage("SERVICES") }
                        )
                    }
                    "RATE_CARD" -> {
                        RateCardScreen(
                            onSelectTrade = { trade -> viewModel.navigateToFindService(trade) },
                            onBack = { viewModel.setCustomerPage("SERVICES") }
                        )
                    }
                    "PROFILE" -> {
                        ProfileScreen(
                            userName = currentUserName,
                            userPhone = currentUserPhone,
                            userRole = userRole,
                            isLoggedIn = isLoggedIn,
                            onRoleToggle = { role -> viewModel.switchRole(role) },
                            onOpenCalendar = {
                                viewModel.switchRole("WORKER")
                                viewModel.setWorkerPage("CALENDAR")
                            },
                            onOpenLogin = { viewModel.openLoginScreen() },
                            onLogout = { viewModel.logoutUser() }
                        )
                    }
                }
            } else {
                // Worker Portal (Sahakari Mode)
                currentWorker?.let { worker ->
                    when (currentWorkerPage) {
                        "CALENDAR" -> {
                            WorkerCalendarScreen(
                                worker = worker,
                                schedules = workerSchedules,
                                onUpdateSchedule = { dateKey, status ->
                                    viewModel.updateWorkerSchedule(dateKey, status)
                                }
                            )
                        }
                        "PROFILE" -> {
                            ProfileScreen(
                                userName = "Rameshwar Sharma",
                                userPhone = "+91 98112 23344",
                                userRole = userRole,
                                isLoggedIn = isLoggedIn,
                                onRoleToggle = { role -> viewModel.switchRole(role) },
                                onOpenCalendar = { viewModel.setWorkerPage("CALENDAR") },
                                onOpenLogin = { viewModel.openLoginScreen() },
                                onLogout = { viewModel.logoutUser() }
                            )
                        }
                        else -> {
                            // Map currentWorkerPage to activeSubTab
                            val subTab = when (currentWorkerPage) {
                                "WALLET" -> "DIVIDEND_WALLET"
                                "COOP_HUB" -> "COOP_VOTING"
                                else -> "JOB_REQUESTS"
                            }
                            WorkerPortalScreen(
                                worker = worker,
                                bookings = allBookings,
                                votes = allVotes,
                                activeSubTab = subTab,
                                onSubTabChange = { tab ->
                                    val page = when (tab) {
                                        "DIVIDEND_WALLET" -> "WALLET"
                                        "COOP_VOTING" -> "COOP_HUB"
                                        else -> "JOBS"
                                    }
                                    viewModel.setWorkerPage(page)
                                },
                                onToggleAvailability = { isAvailable ->
                                    viewModel.toggleAvailability(worker.id, isAvailable)
                                },
                                onAdvanceBookingStatus = { id, status ->
                                    viewModel.updateBookingStatus(id, status)
                                },
                                onVerifyOtp = { booking, otp ->
                                    viewModel.verifyOtpAndStartJob(booking, otp)
                                },
                                onVote = { vote, choice ->
                                    viewModel.voteOnResolution(vote, choice)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets
    if (showBookingFlow) {
        BookingFlowSheet(
            allWorkers = allWorkers,
            initialWorker = selectedWorkerForBooking,
            initialTrade = bookingFlowTrade,
            onDismiss = { viewModel.closeBookingFlow() },
            onConfirmBooking = { worker, serviceTitle, desc, date, slot, name, phone, address, total, paymentMethod ->
                viewModel.createBooking(
                    worker = worker,
                    serviceTitle = serviceTitle,
                    description = desc,
                    date = date,
                    timeSlot = slot,
                    customerName = name,
                    customerPhone = phone,
                    customerAddress = address,
                    estimatedTotal = total,
                    paymentMethod = paymentMethod
                )
                viewModel.closeBookingFlow()
            }
        )
    }

    selectedWorkerForDetails?.let { worker ->
        WorkerDetailsBottomSheet(
            worker = worker,
            onDismiss = { viewModel.openWorkerDetails(null) },
            onBookClick = {
                viewModel.openWorkerDetails(null)
                viewModel.openBookingFlow(worker.trade, worker)
            }
        )
    }

    if (showRateCardDialog) {
        RateCardBottomSheet(
            onDismiss = { viewModel.toggleRateCard(false) }
        )
    }
}

