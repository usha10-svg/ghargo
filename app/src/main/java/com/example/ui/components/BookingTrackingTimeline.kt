package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEntity
import com.example.data.BookingTrackingStatus
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight

/**
 * Visual 5-Step Horizontal Stepper for Booking Tracking:
 * 1. Confirmed
 * 2. Worker Assigned
 * 3. Worker Arriving
 * 4. Service Started
 * 5. Completed
 */
@Composable
fun BookingTrackingTimeline(
    currentStatus: BookingTrackingStatus,
    modifier: Modifier = Modifier
) {
    val steps = BookingTrackingStatus.orderedSteps

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        // Step Nodes Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, step ->
                val isCompleted = step.stepNumber < currentStatus.stepNumber
                val isCurrent = step.stepNumber == currentStatus.stepNumber
                val isUpcoming = step.stepNumber > currentStatus.stepNumber

                val circleBg by animateColorAsState(
                    targetValue = when {
                        isCompleted -> WelfareGreen
                        isCurrent -> SaffronTrust
                        else -> Color(0xFFE2E8F0)
                    },
                    label = "circleColor"
                )

                val iconTint = when {
                    isCompleted || isCurrent -> Color.White
                    else -> Color(0xFF94A3B8)
                }

                // Step Circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(circleBg)
                            .then(
                                if (isCurrent) {
                                    Modifier.border(2.dp, CooperativeNavy, CircleShape)
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = iconTint,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = getStatusStepIcon(step),
                                contentDescription = step.title,
                                tint = iconTint,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = step.title,
                        fontSize = 9.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) CooperativeNavy else if (isCompleted) WelfareGreen else TextMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = 11.sp,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }

                // Connector line between steps
                if (index < steps.size - 1) {
                    val lineFilled = step.stepNumber < currentStatus.stepNumber
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(10.dp)
                            .background(if (lineFilled) WelfareGreen else Color(0xFFE2E8F0))
                    )
                }
            }
        }
    }
}

/**
 * Status icons for the 5 stages
 */
fun getStatusStepIcon(status: BookingTrackingStatus): ImageVector {
    return when (status) {
        BookingTrackingStatus.CONFIRMED -> Icons.Default.Receipt
        BookingTrackingStatus.WORKER_ASSIGNED -> Icons.Default.Person
        BookingTrackingStatus.WORKER_ARRIVING -> Icons.Default.DirectionsCar
        BookingTrackingStatus.SERVICE_STARTED -> Icons.Default.Build
        BookingTrackingStatus.COMPLETED -> Icons.Default.CheckCircle
    }
}

/**
 * Dynamic live message banner indicating current progress
 */
@Composable
fun BookingStatusLiveBanner(
    status: BookingTrackingStatus,
    workerName: String,
    otpCode: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, icon, title, subtitle) = when (status) {
        BookingTrackingStatus.CONFIRMED -> Tuple5(
            Color(0xFFFEF3C7),
            Color(0xFFFDE68A),
            Icons.Default.Receipt,
            "1. Booking Confirmed",
            "Cooperative has registered your service. Artisan dispatch in progress."
        )
        BookingTrackingStatus.WORKER_ASSIGNED -> Tuple5(
            Color(0xFFEFF6FF),
            Color(0xFFBFDBFE),
            Icons.Default.Person,
            "2. Worker Assigned",
            "Artisan $workerName has accepted the service order."
        )
        BookingTrackingStatus.WORKER_ARRIVING -> Tuple5(
            Color(0xFFFFF7ED),
            Color(0xFFFED7AA),
            Icons.Default.DirectionsCar,
            "3. Worker Arriving",
            "Artisan is traveling to your location. Keep OTP $otpCode ready."
        )
        BookingTrackingStatus.SERVICE_STARTED -> Tuple5(
            Color(0xFFF5F3FF),
            Color(0xFFDDD6FE),
            Icons.Default.Build,
            "4. Service Started",
            "Security OTP verified. Work is actively underway on-site."
        )
        BookingTrackingStatus.COMPLETED -> Tuple5(
            WelfareGreenLight,
            Color(0xFFA7F3D0),
            Icons.Default.CheckCircle,
            "5. Service Completed",
            "Work inspected & approved. Artisan paid transparently."
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CooperativeNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CooperativeNavy
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A, val b: B, val c: C, val d: D, val e: E
)

/**
 * Full Tracking Bottom Sheet Modal for interactive tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingTrackingBottomSheet(
    booking: BookingEntity,
    onDismiss: () -> Unit,
    onAdvanceStatus: (Long, String) -> Unit,
    onCompleteAndRate: (Long, Long, Int, Float, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentStatus = BookingTrackingStatus.from(booking.status)

    var rating by remember { mutableFloatStateOf(5f) }
    var reviewText by remember { mutableStateOf("Great work and transparent cooperative pricing!") }
    var showRatingSection by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 600.dp)
                .align(Alignment.CenterHorizontally)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Booking #${booking.id} Tracking",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CooperativeNavy
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadgeChip(status = currentStatus)
                    }
                    Text(
                        text = "Delhi Shramik Sahakari • Real-Time Artisan Tracking",
                        fontSize = 11.sp,
                        color = SaffronTrust,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5-Stage Stepper
            BookingTrackingTimeline(currentStatus = currentStatus)

            Spacer(modifier = Modifier.height(14.dp))

            // Live Banner
            BookingStatusLiveBanner(
                status = currentStatus,
                workerName = booking.workerName,
                otpCode = booking.otpCode
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Assigned Artisan Mini-Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(CooperativeNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = booking.workerName.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = booking.workerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CooperativeNavy
                                )
                                Text(
                                    text = "${booking.trade} • Verified Member-Owner",
                                    fontSize = 11.sp,
                                    color = SaffronTrust,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Call button
                        IconButton(
                            onClick = { /* Demo phone call trigger */ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WelfareGreenLight)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Artisan",
                                tint = WelfareGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SurfaceBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Verification Badges Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VerificationTag(text = "e-Shram Verified", icon = Icons.Default.Verified)
                        VerificationTag(text = "NSDC Certified", icon = Icons.Default.Engineering)
                        VerificationTag(text = "Guild Co-Owner", icon = Icons.Default.AccountBalance)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Service details & address
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = booking.serviceTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CooperativeNavy
                    )
                    if (booking.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = booking.description,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = SaffronTrust,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${booking.scheduledDate} • ${booking.scheduledTime}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = booking.customerAddress,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Security OTP Box (shown for arriving / scheduled)
            if (currentStatus != BookingTrackingStatus.COMPLETED) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Start-Job Security OTP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "Share with worker ONLY after arrival",
                                    fontSize = 10.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CooperativeNavy)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = booking.otpCode,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Advancement Actions (Simulate / Follow along status workflow)
            Text(
                text = "Tracking Actions & Status Progression",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CooperativeNavy
            )
            Text(
                text = "Track or advance booking across the 5 cooperative stages.",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            when (currentStatus) {
                BookingTrackingStatus.CONFIRMED -> {
                    Button(
                        onClick = { onAdvanceStatus(booking.id, BookingTrackingStatus.WORKER_ASSIGNED.code) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("advance_to_worker_assigned"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Advance: Assign Worker (${booking.workerName})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                BookingTrackingStatus.WORKER_ASSIGNED -> {
                    Button(
                        onClick = { onAdvanceStatus(booking.id, BookingTrackingStatus.WORKER_ARRIVING.code) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("advance_to_worker_arriving"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CooperativeNavy)
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Advance: Worker Arriving (En Route)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                BookingTrackingStatus.WORKER_ARRIVING -> {
                    Button(
                        onClick = { onAdvanceStatus(booking.id, BookingTrackingStatus.SERVICE_STARTED.code) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("advance_to_service_started"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify OTP (${booking.otpCode}) & Start Service", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                BookingTrackingStatus.SERVICE_STARTED -> {
                    Column {
                        Button(
                            onClick = { showRatingSection = !showRatingSection },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("open_completion_section"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WelfareGreen)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Complete Service & Settle Payment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        AnimatedVisibility(visible = showRatingSection) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Rate Artisan Performance",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
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
                                            tint = if (star <= rating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable { rating = star.toFloat() }
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "$rating / 5.0", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                        onCompleteAndRate(booking.id, booking.workerId, booking.totalAmount, rating, reviewText)
                                        showRatingSection = false
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WelfareGreen)
                                ) {
                                    Text("Pay ₹${booking.totalAmount} & Mark Completed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                BookingTrackingStatus.COMPLETED -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(WelfareGreenLight)
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = WelfareGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Booking Fully Completed & Paid",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = WelfareGreen
                                )
                                Text(
                                    text = "₹${booking.workerShare} paid to artisan, ₹${booking.welfareContribution} credited to Welfare Fund.",
                                    fontSize = 11.sp,
                                    color = WelfareGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadgeChip(status: BookingTrackingStatus) {
    val (bg, fg) = when (status) {
        BookingTrackingStatus.CONFIRMED -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        BookingTrackingStatus.WORKER_ASSIGNED -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        BookingTrackingStatus.WORKER_ARRIVING -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
        BookingTrackingStatus.SERVICE_STARTED -> Color(0xFFEDE9FE) to Color(0xFF5B21B6)
        BookingTrackingStatus.COMPLETED -> WelfareGreenLight to WelfareGreen
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.title,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun VerificationTag(text: String, icon: ImageVector) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = WelfareGreen, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = text, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
    }
}
