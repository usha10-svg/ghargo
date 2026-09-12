package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkerEntity
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Worker schedule availability statuses
 */
enum class WorkerScheduleStatus(
    val title: String,
    val subtitle: String,
    val color: Color,
    val containerColor: Color,
    val icon: ImageVector,
    val badgeLabel: String
) {
    AVAILABLE(
        title = "Available",
        subtitle = "Open for regular customer bookings (8 AM - 8 PM)",
        color = WelfareGreen,
        containerColor = Color(0xFFECFDF5),
        icon = Icons.Default.EventAvailable,
        badgeLabel = "Available"
    ),
    BUSY(
        title = "Busy",
        subtitle = "Occupied with confirmed jobs or training",
        color = SaffronTrust,
        containerColor = Color(0xFFFFFBEB),
        icon = Icons.Default.EventBusy,
        badgeLabel = "Busy"
    ),
    LEAVE(
        title = "Leave",
        subtitle = "Off-duty, resting or family occasion (No jobs assigned)",
        color = Color(0xFFDC2626),
        containerColor = Color(0xFFFEF2F2),
        icon = Icons.Default.DoNotDisturb,
        badgeLabel = "On Leave"
    ),
    EMERGENCY_ONLY(
        title = "Available for Emergency Jobs",
        subtitle = "On urgent standby for 24/7 high-priority emergency calls",
        color = Color(0xFF2563EB),
        containerColor = Color(0xFFEFF6FF),
        icon = Icons.Default.ElectricBolt,
        badgeLabel = "Emergency 24/7"
    )
}

data class CalendarDayItem(
    val dateKey: String, // e.g. "2026-09-12"
    val dayOfWeek: String, // e.g. "Sat"
    val dayNumber: Int, // e.g. 12
    val monthName: String, // e.g. "Sep"
    val isToday: Boolean
)

data class TimeSlotItem(
    val id: String,
    val label: String,
    val timing: String,
    val icon: ImageVector
)

/**
 * Modern, clean, and intuitive Worker Calendar Screen.
 * Artisans can easily tap a day and toggle their status:
 * Available, Busy, Leave, or Available for Emergency Jobs.
 */
@Composable
fun WorkerCalendarScreen(
    worker: WorkerEntity,
    schedules: Map<String, WorkerScheduleStatus>,
    onUpdateSchedule: (dateKey: String, status: WorkerScheduleStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    // Generate next 14 days
    val calendarDays = remember {
        val list = mutableListOf<CalendarDayItem>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
        val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
        val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        for (i in 0 until 14) {
            val date = cal.time
            list.add(
                CalendarDayItem(
                    dateKey = keyFormat.format(date),
                    dayOfWeek = dayFormat.format(date),
                    dayNumber = cal.get(Calendar.DAY_OF_MONTH),
                    monthName = monthFormat.format(date),
                    isToday = i == 0
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    var selectedDayKey by remember { mutableStateOf(calendarDays.first().dateKey) }
    val selectedDayItem = calendarDays.firstOrNull { it.dateKey == selectedDayKey } ?: calendarDays.first()

    // Current status for selected day
    val currentStatus = schedules[selectedDayKey] ?: if (worker.isAvailable) WorkerScheduleStatus.AVAILABLE else WorkerScheduleStatus.BUSY

    val timeSlots = listOf(
        TimeSlotItem("slot_morning", "Morning Slot", "08:00 AM - 12:00 PM", Icons.Default.Schedule),
        TimeSlotItem("slot_afternoon", "Afternoon Slot", "12:00 PM - 04:00 PM", Icons.Default.Schedule),
        TimeSlotItem("slot_evening", "Evening Slot", "04:00 PM - 08:00 PM", Icons.Default.Schedule),
        TimeSlotItem("slot_night", "Night Emergency Standby", "08:00 PM - 08:00 AM", Icons.Default.ElectricBolt)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
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
                        Column {
                            Text(
                                text = "Artisan Availability Calendar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CooperativeNavy
                            )
                            Text(
                                text = "${worker.name} • ${worker.trade}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        // Live status pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(currentStatus.containerColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(currentStatus.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentStatus.badgeLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = currentStatus.color
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SurfaceBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Mark your days in advance so customers know when you're available or on leave, or switch to Emergency Standby for 1.5x cooperative bonus.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Horizontal Date Strip (14 days)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Text(
                    text = "Select Date",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CooperativeNavy,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    calendarDays.forEach { day ->
                        val isSelected = day.dateKey == selectedDayKey
                        val dayStatus = schedules[day.dateKey] ?: WorkerScheduleStatus.AVAILABLE

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CooperativeNavy else Color.White
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CooperativeNavy else SurfaceBorder
                            ),
                            modifier = Modifier
                                .width(62.dp)
                                .clickable { selectedDayKey = day.dateKey }
                                .testTag("calendar_day_${day.dateKey}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = day.dayOfWeek,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF94A3B8) else TextMuted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${day.dayNumber}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Small status dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(dayStatus.color)
                                )

                                if (day.isToday) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "TODAY",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SaffronTrust else SaffronTrust
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Day Schedule Options
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
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
                        Text(
                            text = "Set Status for ${selectedDayItem.dayOfWeek}, ${selectedDayItem.monthName} ${selectedDayItem.dayNumber}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CooperativeNavy
                        )
                        if (selectedDayItem.isToday) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SaffronTrust.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Current Day",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronTrust
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Status Action Options
                    WorkerScheduleStatus.values().forEach { status ->
                        val isCurrentStatus = currentStatus == status

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentStatus) status.containerColor else Color(0xFFF8FAFC)
                            ),
                            border = BorderStroke(
                                width = if (isCurrentStatus) 1.5.dp else 1.dp,
                                color = if (isCurrentStatus) status.color else SurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onUpdateSchedule(selectedDayKey, status)
                                }
                                .testTag("schedule_status_${status.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrentStatus) status.color else Color.White)
                                        .border(
                                            1.dp,
                                            if (isCurrentStatus) status.color else SurfaceBorder,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = status.icon,
                                        contentDescription = status.title,
                                        tint = if (isCurrentStatus) Color.White else status.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = status.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrentStatus) CooperativeNavy else TextPrimary
                                        )
                                        if (status == WorkerScheduleStatus.EMERGENCY_ONLY) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF2563EB))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "+50% Surge Pay",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = status.subtitle,
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        lineHeight = 14.sp
                                    )
                                }

                                if (isCurrentStatus) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = status.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Time Slot Breakdown for the selected day
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Working Hours & Shifts",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CooperativeNavy
                    )
                    Text(
                        text = "Your daily shift breakdown according to cooperative guidelines",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    timeSlots.forEach { slot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = slot.icon,
                                    contentDescription = null,
                                    tint = if (slot.id == "slot_night") Color(0xFF2563EB) else SaffronTrust,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = slot.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CooperativeNavy
                                    )
                                    Text(
                                        text = slot.timing,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            val isSlotActive = when (currentStatus) {
                                WorkerScheduleStatus.AVAILABLE -> slot.id != "slot_night"
                                WorkerScheduleStatus.EMERGENCY_ONLY -> true
                                WorkerScheduleStatus.BUSY -> slot.id == "slot_morning" || slot.id == "slot_afternoon"
                                WorkerScheduleStatus.LEAVE -> false
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSlotActive) WelfareGreenLight else Color(0xFFF1F5F9))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isSlotActive) "Active Shift" else "Inactive",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSlotActive) WelfareGreen else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Bulk Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Mark all 14 days as Available
                        calendarDays.forEach {
                            onUpdateSchedule(it.dateKey, WorkerScheduleStatus.AVAILABLE)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, WelfareGreen)
                ) {
                    Text("Set All Available", fontSize = 11.sp, color = WelfareGreen, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        // Set current day to Emergency Standby
                        onUpdateSchedule(selectedDayKey, WorkerScheduleStatus.EMERGENCY_ONLY)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Go on Emergency", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
