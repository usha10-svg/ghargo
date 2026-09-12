package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkerEntity
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.CooperativeNavyDark
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight
import kotlin.math.roundToInt

/**
 * Mock Geo Location definition for prototype map positioning.
 * Centered around Mayur Vihar / East Delhi (28.6139° N, 77.2090° E reference).
 */
data class MockWorkerLocation(
    val workerId: Long,
    val lat: Double,
    val lng: Double,
    val localityLabel: String,
    val distanceKm: Double,
    val etaMinutes: Int,
    val isAvailable: Boolean = true
)

object MockLocationService {
    // User anchor location: Mayur Vihar Phase 1, Delhi
    val userLocation = Pair(28.6085, 77.2940)
    const val userLocality = "Mayur Vihar Phase 1, New Delhi"

    val workerLocations: Map<Long, MockWorkerLocation> = mapOf(
        1L to MockWorkerLocation(1L, 28.6145, 77.2995, "Mayur Vihar Ext.", 0.8, 8, isAvailable = true),
        7L to MockWorkerLocation(7L, 28.6020, 77.2870, "Pandav Nagar", 0.9, 10, isAvailable = true),
        2L to MockWorkerLocation(2L, 28.6180, 77.2880, "Patparganj", 1.1, 12, isAvailable = true),
        8L to MockWorkerLocation(8L, 28.5990, 77.3050, "Chilla Village", 1.2, 14, isAvailable = true),
        3L to MockWorkerLocation(3L, 28.6250, 77.3010, "IP Extension", 1.4, 15, isAvailable = true),
        11L to MockWorkerLocation(11L, 28.5910, 77.2910, "Kalyanpuri", 1.5, 17, isAvailable = true),
        9L to MockWorkerLocation(9L, 28.6270, 77.2840, "Preet Vihar", 1.6, 18, isAvailable = true),
        4L to MockWorkerLocation(4L, 28.5860, 77.3090, "New Ashok Nagar", 1.9, 21, isAvailable = true),
        10L to MockWorkerLocation(10L, 28.6340, 77.2980, "Anand Vihar", 2.2, 24, isAvailable = false),
        5L to MockWorkerLocation(5L, 28.5810, 77.2790, "Trilokpuri", 2.3, 26, isAvailable = true),
        6L to MockWorkerLocation(6L, 28.6420, 77.2870, "Geeta Colony", 2.7, 30, isAvailable = false),
        12L to MockWorkerLocation(12L, 28.6280, 77.3450, "Noida Sector 62", 3.1, 35, isAvailable = true)
    )

    fun getLocationFor(worker: WorkerEntity): MockWorkerLocation {
        return workerLocations[worker.id] ?: MockWorkerLocation(
            workerId = worker.id,
            lat = userLocation.first + ((worker.id * 7 % 20) - 10) * 0.003,
            lng = userLocation.second + ((worker.id * 11 % 20) - 10) * 0.003,
            localityLabel = worker.locality.substringBefore(","),
            distanceKm = ((worker.id * 3 % 20) + 8) / 10.0,
            etaMinutes = (((worker.id * 3 % 20) + 8) * 10).toInt(),
            isAvailable = worker.isAvailable
        )
    }
}

/**
 * Visual interactive map component displaying nearby verified cooperative workers
 * with distance, availability chips, category filtering, and direct booking actions.
 */
@Composable
fun NearbyWorkersMapPrototype(
    workers: List<WorkerEntity>,
    selectedTrade: String = "ALL",
    onSelectTrade: (String) -> Unit = {},
    onWorkerClick: (WorkerEntity) -> Unit,
    onBookClick: (WorkerEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedWorkerId by remember { mutableStateOf<Long?>(null) }
    var filterOnlyAvailable by remember { mutableStateOf(false) }

    // Map boundaries for normalized coordinate projection
    val minLat = 28.570
    val maxLat = 28.650
    val minLng = 27.260.coerceAtLeast(77.260)
    val maxLng = 77.350

    // Filter workers
    val displayedWorkers = remember(workers, selectedTrade, filterOnlyAvailable) {
        workers.filter { worker ->
            val matchesTrade = selectedTrade == "ALL" || worker.trade.equals(selectedTrade, ignoreCase = true)
            val matchesAvailability = !filterOnlyAvailable || worker.isAvailable
            matchesTrade && matchesAvailability
        }
    }

    val selectedWorker = remember(selectedWorkerId, workers) {
        workers.firstOrNull { it.id == selectedWorkerId } ?: displayedWorkers.firstOrNull()
    }

    val trades = listOf("ALL", "Electrician", "Technician", "Plumber", "Carpenter", "Painter", "Cleaner")

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .testTag("nearby_workers_map_container")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CooperativeNavy.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = CooperativeNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Live Worker Radar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CooperativeNavy
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(WelfareGreenLight)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "GPS PROTOTYPE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = WelfareGreen
                                )
                            }
                        }
                        Text(
                            text = "Showing verified guild artisans near ${MockLocationService.userLocality.substringBefore(",")}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                // Availability toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (filterOnlyAvailable) WelfareGreenLight else Color(0xFFE2E8F0))
                        .clickable { filterOnlyAvailable = !filterOnlyAvailable }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (filterOnlyAvailable) "🟢 Available Only" else "All Artisans",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (filterOnlyAvailable) WelfareGreen else TextSecondary
                    )
                }
            }

            // Quick Trade Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trades.forEach { trade ->
                    val isSelected = selectedTrade.equals(trade, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CooperativeNavy else Color(0xFFF1F5F9))
                            .clickable { onSelectTrade(trade) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = trade,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }

            // Interactive Map Canvas Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color(0xFFE8ECEF))
            ) {
                // Vector Map Canvas (Roads, Greenery, River Yamuna, Neighborhoods)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("map_canvas")
                ) {
                    drawStyledVectorMap(size.width, size.height)
                }

                // Center Location Pin (User's Current Location: Mayur Vihar)
                // User Pin at relative center (50% x, 55% y)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CooperativeNavyDark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "You (Mayur Vihar)",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B82F6).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1D4ED8))
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        }
                    }
                }

                // Map Legend Overlay (Top Left)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(WelfareGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ready (Online)", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE11D48))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Busy", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Floating Re-Center Button (Top Right)
                IconButton(
                    onClick = { selectedWorkerId = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, SurfaceBorder, CircleShape)
                        .testTag("recenter_map_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Recenter Map",
                        tint = CooperativeNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Render Worker Markers positioned across the Delhi NCR Canvas
                displayedWorkers.forEach { worker ->
                    val mockLoc = MockLocationService.getLocationFor(worker)
                    val isSelected = worker.id == selectedWorker?.id

                    // Map lat/lng to normalized (xRatio, yRatio) on canvas:
                    // In Delhi NCR layout:
                    // Lng spans from 77.260 (west) to 77.350 (east)
                    // Lat spans from 28.570 (south) to 28.650 (north)
                    val xRatio = ((mockLoc.lng - 77.260) / (77.350 - 77.260)).coerceIn(0.08, 0.90).toFloat()
                    val yRatio = (1.0 - ((mockLoc.lat - 28.570) / (28.650 - 28.570))).coerceIn(0.12, 0.88).toFloat()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        // Position pin based on fractional offsets
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset {
                                    IntOffset(
                                        x = (xRatio * 700).roundToInt().coerceIn(0, 680),
                                        y = (yRatio * 200).roundToInt().coerceIn(0, 180)
                                    )
                                }
                                .clickable {
                                    selectedWorkerId = worker.id
                                }
                                .testTag("worker_pin_${worker.id}")
                        ) {
                            WorkerMapPin(
                                worker = worker,
                                mockLoc = mockLoc,
                                isSelected = isSelected
                            )
                        }
                    }
                }
            }

            // Interactive Bottom Drawer for Selected Worker
            if (selectedWorker != null) {
                val loc = MockLocationService.getLocationFor(selectedWorker)
                SelectedWorkerMapPreview(
                    worker = selectedWorker,
                    location = loc,
                    onViewProfile = { onWorkerClick(selectedWorker) },
                    onBookNow = { onBookClick(selectedWorker) }
                )
            } else {
                // Empty state prompt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tap on any pin or adjust filters to explore nearby artisans",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

/**
 * Vector Map Canvas drawing streets, parks, Yamuna river, and Delhi locality zones.
 */
private fun DrawScope.drawStyledVectorMap(width: Float, height: Float) {
    // 1. Base Map Background
    drawRect(color = Color(0xFFF1F5F9))

    // 2. Parks & Greenery areas (Indraprastha Park, Sanjay Lake)
    drawCircle(
        color = Color(0xFFDCFCE7),
        radius = width * 0.18f,
        center = Offset(width * 0.22f, height * 0.35f)
    )
    drawCircle(
        color = Color(0xFFDCFCE7),
        radius = width * 0.15f,
        center = Offset(width * 0.80f, height * 0.70f)
    )

    // 3. Yamuna River Flow Curve
    val riverPath = Path().apply {
        moveTo(width * 0.38f, 0f)
        cubicTo(
            width * 0.42f, height * 0.35f,
            width * 0.35f, height * 0.65f,
            width * 0.30f, height
        )
    }
    drawPath(
        path = riverPath,
        color = Color(0xFFBFDBFE),
        style = Stroke(width = 28f, cap = StrokeCap.Round)
    )

    // 4. Major Arterial Expressways (NH-24 / Delhi-Meerut Expressway & Ring Road)
    val nh24Path = Path().apply {
        moveTo(0f, height * 0.42f)
        lineTo(width, height * 0.48f)
    }
    drawPath(
        path = nh24Path,
        color = Color(0xFFCBD5E1),
        style = Stroke(width = 10f, cap = StrokeCap.Round)
    )

    val ringRoadPath = Path().apply {
        moveTo(width * 0.15f, 0f)
        lineTo(width * 0.20f, height)
    }
    drawPath(
        path = ringRoadPath,
        color = Color(0xFFE2E8F0),
        style = Stroke(width = 7f, cap = StrokeCap.Round)
    )

    // Secondary Cross Roads
    val secRoad1 = Path().apply {
        moveTo(0f, height * 0.75f)
        lineTo(width, height * 0.72f)
    }
    drawPath(path = secRoad1, color = Color(0xFFFFFFFF), style = Stroke(width = 5f))

    val secRoad2 = Path().apply {
        moveTo(width * 0.65f, 0f)
        lineTo(width * 0.62f, height)
    }
    drawPath(path = secRoad2, color = Color(0xFFFFFFFF), style = Stroke(width = 5f))

    // Metro Line Elevated Track (Blue Line & Pink Line)
    val metroLine = Path().apply {
        moveTo(0f, height * 0.40f)
        cubicTo(width * 0.45f, height * 0.44f, width * 0.65f, height * 0.50f, width, height * 0.55f)
    }
    drawPath(
        path = metroLine,
        color = Color(0xFF2563EB).copy(alpha = 0.5f),
        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
    )
}

/**
 * Individual Map Marker Pin showing trade icon, availability indicator, and name badge.
 */
@Composable
private fun WorkerMapPin(
    worker: WorkerEntity,
    mockLoc: MockWorkerLocation,
    isSelected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        // Tag label
        Box(
            modifier = Modifier
                .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) CooperativeNavy else Color.White)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) SaffronTrust else SurfaceBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Availability dot
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (mockLoc.isAvailable) WelfareGreen else Color(0xFFE11D48))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${worker.name.substringBefore(" ")} (${mockLoc.distanceKm}km)",
                    fontSize = if (isSelected) 10.sp else 9.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = if (isSelected) Color.White else TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Custom Pin Circle
        Box(
            modifier = Modifier
                .size(if (isSelected) 34.dp else 28.dp)
                .shadow(if (isSelected) 6.dp else 3.dp, CircleShape)
                .clip(CircleShape)
                .background(if (isSelected) SaffronTrust else CooperativeNavy)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getTradeIcon(worker.trade),
                contentDescription = worker.trade,
                tint = if (isSelected) CooperativeNavy else Color.White,
                modifier = Modifier.size(if (isSelected) 18.dp else 14.dp)
            )
        }
    }
}

/**
 * Preview Card at the bottom of the map displaying full details of the selected artisan.
 */
@Composable
private fun SelectedWorkerMapPreview(
    worker: WorkerEntity,
    location: MockWorkerLocation,
    onViewProfile: () -> Unit,
    onBookNow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("selected_worker_preview_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WorkerPhotoAvatar(
                        worker = worker,
                        size = 46.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = worker.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CooperativeNavy
                            )
                            if (worker.verifiedNsdc) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "NSDC Verified",
                                    tint = WelfareGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = "${worker.trade} • ${worker.experienceYears} yrs exp",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${worker.rating} (${worker.reviewCount})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${worker.memberId}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Rate Badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${worker.hourlyRate}/hr",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CooperativeNavy
                    )
                    Text(
                        text = "0% middleman",
                        fontSize = 9.sp,
                        color = WelfareGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Proximity, Locality & Availability Status Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = null,
                        tint = SaffronTrust,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${location.distanceKm} km away",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${location.etaMinutes} min arrival)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Availability chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (location.isAvailable) WelfareGreenLight else Color(0xFFFFE4E6))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (location.isAvailable) WelfareGreen else Color(0xFFE11D48))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (location.isAvailable) "Available Now" else "On Active Job",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (location.isAvailable) WelfareGreen else Color(0xFFBE123C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Profile Details + Direct Booking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("map_view_profile_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Full Profile", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CooperativeNavy)
                }

                Button(
                    onClick = onBookNow,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(40.dp)
                        .testTag("map_book_artisan_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Book Artisan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
