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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEntity
import com.example.data.BookingTrackingStatus
import com.example.ui.components.BookingStatusLiveBanner
import com.example.ui.components.BookingTrackingBottomSheet
import com.example.ui.components.BookingTrackingTimeline
import com.example.ui.components.StatusBadgeChip
import com.example.ui.components.getTradeIcon
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight

@Composable
fun CustomerBookingsScreen(
    bookings: List<BookingEntity>,
    onAdvanceStatus: (Long, String) -> Unit,
    onCompleteAndRate: (Long, Long, Int, Float, String) -> Unit,
    onExploreServices: () -> Unit
) {
    var activeTrackingBooking by remember { mutableStateOf<BookingEntity?>(null) }

    // If a booking is currently being tracked in the modal sheet, keep it updated from bookings list
    val currentTrackingEntity = activeTrackingBooking?.let { active ->
        bookings.find { it.id == active.id } ?: active
    }

    if (currentTrackingEntity != null) {
        BookingTrackingBottomSheet(
            booking = currentTrackingEntity,
            onDismiss = { activeTrackingBooking = null },
            onAdvanceStatus = { id, nextStatus -> onAdvanceStatus(id, nextStatus) },
            onCompleteAndRate = { bId, wId, amt, rat, rev ->
                onCompleteAndRate(bId, wId, amt, rat, rev)
            }
        )
    }

    if (bookings.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SaffronTrust.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = SaffronTrust,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Service Bookings Yet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CooperativeNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Book verified electricians, plumbers, carpenters & more with 0% middleman cut.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onExploreServices,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust),
                    modifier = Modifier.testTag("empty_explore_button")
                ) {
                    Text("Explore Verified Artisans", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Cooperative Bookings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CooperativeNavy
                    )
                    Text(
                        text = "Live status tracking: Confirmed → Worker Assigned → Arriving → Started → Completed",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WelfareGreenLight)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${bookings.size} Active/Total",
                        color = WelfareGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(bookings, key = { it.id }) { booking ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                BookingTrackingCard(
                    booking = booking,
                    onOpenTracking = { activeTrackingBooking = booking },
                    onAdvanceStatus = { newStatus -> onAdvanceStatus(booking.id, newStatus) },
                    onCompleteAndRate = { rating, review ->
                        onCompleteAndRate(booking.id, booking.workerId, booking.totalAmount, rating, review)
                    }
                )
            }
        }
    }
}

@Composable
fun BookingTrackingCard(
    booking: BookingEntity,
    onOpenTracking: () -> Unit,
    onAdvanceStatus: (String) -> Unit,
    onCompleteAndRate: (Float, String) -> Unit
) {
    var showRatingSection by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(5f) }
    var reviewText by remember { mutableStateOf("Prompt arrival, great craftsmanship, and transparent billing!") }

    val currentStatus = BookingTrackingStatus.from(booking.status)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top: Trade Icon + Service Title + Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CooperativeNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getTradeIcon(booking.trade),
                        contentDescription = booking.trade,
                        tint = SaffronTrust,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.serviceTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "Artisan: ${booking.workerName} (${booking.trade})",
                        fontSize = 12.sp,
                        color = SaffronTrust,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                StatusBadgeChip(status = currentStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5-Stage Stepper Bar
            BookingTrackingTimeline(currentStatus = currentStatus)

            Spacer(modifier = Modifier.height(10.dp))

            // Live Banner
            BookingStatusLiveBanner(
                status = currentStatus,
                workerName = booking.workerName,
                otpCode = booking.otpCode
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Safety OTP Banner if not completed
            if (currentStatus != BookingTrackingStatus.COMPLETED && booking.status != "CANCELLED") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFFBEB))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Start-Job Security OTP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "Provide code upon artisan arrival",
                                    fontSize = 10.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CooperativeNavy)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = booking.otpCode,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Description & Schedule
            if (booking.description.isNotBlank()) {
                Text(
                    text = booking.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${booking.scheduledDate}, ${booking.scheduledTime}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Total: ₹${booking.totalAmount} (${booking.paymentMethod})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CooperativeNavy
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: "Track Live Progress" + Progression Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpenTracking,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("track_button_${booking.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Track",
                        tint = CooperativeNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Live Tracking", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CooperativeNavy)
                }

                // Inline advancement button depending on 5 stages
                when (currentStatus) {
                    BookingTrackingStatus.CONFIRMED -> {
                        Button(
                            onClick = { onAdvanceStatus(BookingTrackingStatus.WORKER_ASSIGNED.code) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(42.dp)
                        ) {
                            Text("Assign Worker", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    BookingTrackingStatus.WORKER_ASSIGNED -> {
                        Button(
                            onClick = { onAdvanceStatus(BookingTrackingStatus.WORKER_ARRIVING.code) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CooperativeNavy),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Worker Arriving", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    BookingTrackingStatus.WORKER_ARRIVING -> {
                        Button(
                            onClick = { onAdvanceStatus(BookingTrackingStatus.SERVICE_STARTED.code) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start Service", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    BookingTrackingStatus.SERVICE_STARTED -> {
                        Button(
                            onClick = { showRatingSection = !showRatingSection },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WelfareGreen),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Complete & Pay", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    BookingTrackingStatus.COMPLETED -> {
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WelfareGreenLight)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Paid & Done (${booking.ratingGiven}★)",
                                color = WelfareGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Rating dialog expansion
            AnimatedVisibility(visible = showRatingSection) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Rate Artisan & Confirm Payment (₹${booking.totalAmount})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CooperativeNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star Stars",
                                tint = if (star <= userRating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable { userRating = star.toFloat() }
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "$userRating / 5.0", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onCompleteAndRate(userRating, reviewText)
                            showRatingSection = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WelfareGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text("Pay ₹${booking.totalAmount} via Jan Dhan UPI & Finish", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val trackingStatus = BookingTrackingStatus.from(status)
    StatusBadgeChip(status = trackingStatus)
}
