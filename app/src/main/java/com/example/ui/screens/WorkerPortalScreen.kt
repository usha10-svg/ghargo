package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEntity
import com.example.data.BookingTrackingStatus
import com.example.data.CooperativeVoteEntity
import com.example.data.WorkerEntity
import com.example.ui.components.BookingTrackingTimeline
import com.example.ui.components.StatusBadgeChip
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.CooperativeNavyDark
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBadgeBlue
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight

@Composable
fun WorkerPortalScreen(
    worker: WorkerEntity,
    bookings: List<BookingEntity>,
    votes: List<CooperativeVoteEntity>,
    activeSubTab: String,
    onSubTabChange: (String) -> Unit,
    onToggleAvailability: (Boolean) -> Unit,
    onAdvanceBookingStatus: (Long, String) -> Unit,
    onVerifyOtp: (BookingEntity, String) -> Boolean,
    onVote: (CooperativeVoteEntity, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Sub-navigation pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "JOB_REQUESTS" to "Active Jobs",
                    "DIVIDEND_WALLET" to "Coop Wallet",
                    "COOP_VOTING" to "Democratic AGM",
                    "ID_CARD" to "Worker ID"
                ).forEach { (key, label) ->
                    val isSelected = activeSubTab == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CooperativeNavy else Color.Transparent)
                            .clickable { onSubTabChange(key) }
                            .padding(vertical = 9.dp)
                            .testTag("worker_tab_$key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        when (activeSubTab) {
            "JOB_REQUESTS" -> {
                item {
                    // Availability Status Switch Bar
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (worker.isAvailable) WelfareGreen else Color.Gray)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (worker.isAvailable) "Duty Status: Available for Jobs" else "Duty Status: Off Duty",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (worker.isAvailable) WelfareGreen else TextSecondary
                                    )
                                }
                                Text(
                                    text = "Ready to receive local Delhi NCR dispatch requests",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Switch(
                                checked = worker.isAvailable,
                                onCheckedChange = { onToggleAvailability(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = WelfareGreen),
                                modifier = Modifier.testTag("duty_status_switch")
                            )
                        }
                    }
                }

                // Incoming / Ongoing Bookings
                val workerJobs = bookings.filter { it.workerId == worker.id }
                if (workerJobs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No Active Service Requests",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Switch to Customer Mode to book a service and see it arrive here!",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(workerJobs) { job ->
                        Box(modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp)) {
                            WorkerJobActionCard(
                                job = job,
                                onAdvanceStatus = { newStatus -> onAdvanceBookingStatus(job.id, newStatus) },
                                onVerifyOtp = { otp -> onVerifyOtp(job, otp) }
                            )
                        }
                    }
                }
            }

            "DIVIDEND_WALLET" -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp)) {
                        WorkerWalletSection(worker = worker)
                    }
                }
            }

            "COOP_VOTING" -> {
                item {
                    Column(modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp)) {
                        Text(
                            text = "Cooperative Democratic Governance",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CooperativeNavy
                        )
                        Text(
                            text = "1 Worker = 1 Vote • Democratic control of wage floors and welfare funds",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                items(votes) { vote ->
                    Box(modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp)) {
                        VotingCard(vote = vote, onVote = { choice -> onVote(vote, choice) })
                    }
                }
            }

            "ID_CARD" -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp)) {
                        WorkerDigitalIdSection(worker = worker)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkerJobActionCard(
    job: BookingEntity,
    onAdvanceStatus: (String) -> Unit,
    onVerifyOtp: (String) -> Boolean
) {
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf(false) }

    val currentStatus = BookingTrackingStatus.from(job.status)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.serviceTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Customer: ${job.customerName} • ${job.customerPhone}",
                        fontSize = 12.sp,
                        color = SaffronTrust,
                        fontWeight = FontWeight.Medium
                    )
                }
                StatusBadgeChip(status = currentStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5-Stage Stepper for Worker View
            BookingTrackingTimeline(currentStatus = currentStatus)

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = job.customerAddress, fontSize = 12.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Earnings breakdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Worker Net Pay (97%)", fontSize = 11.sp, color = TextMuted)
                    Text(text = "₹${job.workerShare}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = WelfareGreen)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Coop Welfare (3%)", fontSize = 11.sp, color = TextMuted)
                    Text(text = "₹${job.welfareContribution}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CooperativeNavy)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Workflow Action Buttons based on 5 statuses
            when (currentStatus) {
                BookingTrackingStatus.CONFIRMED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onAdvanceStatus("CANCELLED") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Decline", color = Color.Red, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { onAdvanceStatus(BookingTrackingStatus.WORKER_ASSIGNED.code) },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WelfareGreen)
                        ) {
                            Text("Accept & Assign Me", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                BookingTrackingStatus.WORKER_ASSIGNED -> {
                    Button(
                        onClick = { onAdvanceStatus(BookingTrackingStatus.WORKER_ARRIVING.code) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CooperativeNavy)
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Travel (Worker Arriving)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                BookingTrackingStatus.WORKER_ARRIVING -> {
                    Column {
                        Text(
                            text = "Enter Customer's 4-Digit Security OTP to Start Service:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = enteredOtp,
                                onValueChange = {
                                    if (it.length <= 4) {
                                        enteredOtp = it
                                        otpError = false
                                    }
                                },
                                placeholder = { Text("e.g. ${job.otpCode}") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Button(
                                onClick = {
                                    val success = onVerifyOtp(enteredOtp)
                                    if (!success) {
                                        otpError = true
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
                            ) {
                                Text("Verify & Start", fontSize = 12.sp)
                            }
                        }
                        if (otpError) {
                            Text(
                                text = "Incorrect OTP! Ask customer for their 4-digit security code.",
                                color = Color.Red,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                BookingTrackingStatus.SERVICE_STARTED -> {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Service in progress on site",
                                color = SaffronTrust,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { onAdvanceStatus(BookingTrackingStatus.COMPLETED.code) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WelfareGreen)
                            ) {
                                Text("Finish Work", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                BookingTrackingStatus.COMPLETED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Payment Received • Rating: ${job.ratingGiven}★",
                            color = WelfareGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${job.workerShare} credited to Jan Dhan",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkerWalletSection(worker: WorkerEntity) {
    var showWithdrawSuccess by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Main Cooperative Balance Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CooperativeNavyDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cooperative Earnings & Dividend",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "₹${worker.completedJobs * 380 + worker.dividendEarned}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SaffronTrust),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Direct Job Income (97%)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text(text = "₹${worker.completedJobs * 380}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text(text = "Lābhānsh (Annual Dividend)", color = SaffronTrust, fontSize = 11.sp)
                        Text(text = "₹${worker.dividendEarned}", color = SaffronTrust, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Equity Shares", color = WelfareGreen, fontSize = 11.sp)
                        Text(text = "${worker.sharesOwned} Shares", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Social Security & Welfare Fund status
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Worker Cooperative Welfare Protection",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CooperativeNavy
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Item 1: Health
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = WelfareGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Ayushman PM-JAY & ESIC Medical Cover", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "₹5,00,000 family cashless hospitalization (Active)", fontSize = 11.sp, color = WelfareGreen)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Item 2: Tool insurance
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = SaffronTrust, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Cooperative Tool Kit Protection", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "Up to ₹15,000 zero-deductible tool theft & drop cover", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Item 3: Pension
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = CooperativeNavy, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "PMSYM Old-Age Pension Auto-Matching", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "Cooperative matches ₹100/mo pension deposit directly", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Instant Payout to Jan Dhan UPI
        Button(
            onClick = { showWithdrawSuccess = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WelfareGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("withdraw_to_jandhan_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Instant Payout to Jan Dhan Bank Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        AnimatedVisibility(visible = showWithdrawSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0FDF4))
                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "Success! Transfer of ₹${worker.completedJobs * 380} initiated via NPCI Jan Dhan UPI to A/C ending in 4112.",
                    color = Color(0xFF166534),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun VotingCard(
    vote: CooperativeVoteEntity,
    onVote: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CooperativeNavy.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = vote.resolutionNumber,
                        color = CooperativeNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "Closes in ${vote.closesInDays} days",
                    fontSize = 11.sp,
                    color = SaffronTrust,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = vote.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = vote.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Voting tally progress bar
            val totalVotes = vote.yesVotes + vote.noVotes
            val yesPercent = if (totalVotes > 0) (vote.yesVotes.toFloat() / totalVotes * 100).toInt() else 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Yes: ${vote.yesVotes} ($yesPercent%)", fontSize = 11.sp, color = WelfareGreen, fontWeight = FontWeight.Bold)
                Text(text = "No: ${vote.noVotes}", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(yesPercent / 100f)
                        .height(8.dp)
                        .background(WelfareGreen)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vote buttons
            if (vote.userVoted != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You voted: ${vote.userVoted} • Recorded in Cooperative Ledger",
                        color = Color(0xFF1E40AF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onVote("NO") },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Vote NO", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onVote("YES") },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WelfareGreen)
                    ) {
                        Text("Vote YES", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkerDigitalIdSection(worker: WorkerEntity) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Government Emblem / Co-op header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SHRAMIK SAHAKARI SAMITI",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = CooperativeNavy
                    )
                    Text(
                        text = "Delhi NCR Skilled Guild Chapter #104",
                        fontSize = 10.sp,
                        color = SaffronTrust,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WelfareGreenLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("VERIFIED MEMBER", color = WelfareGreen, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Photo Placeholder with badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CooperativeNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = SaffronTrust,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = worker.name,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TextPrimary
            )
            Text(
                text = "Certified Master ${worker.trade}",
                fontSize = 13.sp,
                color = SaffronTrust,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Details Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Coop Member ID:", fontSize = 11.sp, color = TextMuted)
                    Text(text = worker.memberId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "e-Shram National UAN:", fontSize = 11.sp, color = TextMuted)
                    Text(text = "2048-9182-4112 (Active)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VerifiedBadgeBlue)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Skill India NSDC Certificate:", fontSize = 11.sp, color = TextMuted)
                    Text(text = "Level 4 (Govt. of India)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WelfareGreen)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Police / ID Verification:", fontSize = 11.sp, color = TextMuted)
                    Text(text = "Cleared (DL-POL-2024)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WelfareGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QR Code Simulation
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Verification QR",
                    tint = CooperativeNavy,
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Scan to verify genuine cooperative membership",
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}
