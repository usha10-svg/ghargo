package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.WorkerEntity
import com.example.ui.components.EmergencyDispatchDialog
import com.example.ui.components.EmergencyServiceItem
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight

/**
 * Dedicated Emergency 24/7 Dispatch Screen.
 * Kept on its own clean page so the regular services screen remains clean and uncluttered.
 */
@Composable
fun EmergencyServicesScreen(
    allWorkers: List<WorkerEntity>,
    onBookEmergencyWorker: (WorkerEntity) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeEmergency by remember { mutableStateOf<EmergencyServiceItem?>(null) }

    val emergencyServices = listOf(
        EmergencyServiceItem(
            id = "em_elec",
            title = "Electrical Short-Circuit / Sparking",
            trade = "Electrician",
            description = "Burnt MCB, power cut in whole flat, electrical smoke, or earthing fault.",
            estimatedArrival = "10-20 min",
            baseRate = 349
        ),
        EmergencyServiceItem(
            id = "em_plumb",
            title = "Severe Pipeline Burst & Flooding",
            trade = "Plumber",
            description = "Main line failure, overhead tank valve overflow, or broken geyser connector.",
            estimatedArrival = "15-25 min",
            baseRate = 299
        ),
        EmergencyServiceItem(
            id = "em_lock",
            title = "Emergency Door Lockout",
            trade = "Carpenter",
            description = "Key broken inside lock, jammed deadbolt, or trapped outside home at night.",
            estimatedArrival = "15-25 min",
            baseRate = 349
        ),
        EmergencyServiceItem(
            id = "em_appliance",
            title = "AC / Fridge Total Failure",
            trade = "Technician",
            description = "Severe compressor burn, gas smell, or refrigerator breakdown with spoiled supplies.",
            estimatedArrival = "20-30 min",
            baseRate = 399
        ),
        EmergencyServiceItem(
            id = "em_care",
            title = "Urgent Elder Mobility Assistance",
            trade = "Caregiver",
            description = "Immediate attendant requirement for bedridden patient support or emergency mobility.",
            estimatedArrival = "20-35 min",
            baseRate = 449
        )
    )

    // Emergency dispatch dialog
    activeEmergency?.let { emergency ->
        val nearestWorker = allWorkers
            .filter { it.trade.equals(emergency.trade, ignoreCase = true) }
            .minByOrNull { getWorkerDistanceKm(it.id) }
            ?: allWorkers.firstOrNull()

        EmergencyDispatchDialog(
            emergency = emergency,
            nearestWorker = nearestWorker,
            onDismiss = { activeEmergency = null },
            onConfirmDispatch = { worker ->
                activeEmergency = null
                onBookEmergencyWorker(worker)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back navigation bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("emergency_screen_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CooperativeNavy
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Emergency 24/7 Dispatch",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CooperativeNavy
                )
            }
        }

        // Emergency Banner Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDC2626)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "24/7 Sahakari SOS Dispatch",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "Average response time: 18 minutes",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDC2626))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "LIVE STANDBY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Emergency calls are instantly routed to on-duty standby cooperative artisans located within 3 km of your GPS location.",
                        fontSize = 11.sp,
                        color = Color(0xFF7F1D1D),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Section title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Urgent Issue",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CooperativeNavy
                )
                Text(
                    text = "Zero Surge Pricing",
                    fontSize = 11.sp,
                    color = WelfareGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Emergency Items
        items(emergencyServices) { item ->
            val nearest = allWorkers
                .filter { it.trade.equals(item.trade, ignoreCase = true) }
                .minByOrNull { getWorkerDistanceKm(it.id) }
            val dist = nearest?.let { getWorkerDistanceKm(it.id) } ?: 1.2

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .clickable { activeEmergency = item }
                    .testTag("emergency_item_${item.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.trade.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ETA ${item.estimatedArrival}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WelfareGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CooperativeNavy
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.description,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${item.baseRate}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SaffronTrust
                            )
                            Text(
                                text = "Fixed Base",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SurfaceBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = SaffronTrust,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${nearest?.name ?: "Artisan"} is ${dist} km away",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Button(
                            onClick = { activeEmergency = item },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Dispatch Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
