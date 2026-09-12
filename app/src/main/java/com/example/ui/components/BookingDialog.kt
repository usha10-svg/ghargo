package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextDecoration
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingBottomSheet(
    worker: WorkerEntity,
    onDismiss: () -> Unit,
    onConfirmBooking: (
        serviceTitle: String,
        description: String,
        date: String,
        timeSlot: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        total: Int,
        paymentMethod: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var serviceTitle by remember {
        mutableStateOf(
            when (worker.trade) {
                "Electrician" -> "Electrical Inspection & Fault Diagnosis"
                "Plumber" -> "Sanitary Fixture & Leakage Repair"
                "Carpenter" -> "Furniture & Fitting Restoration"
                "Painter" -> "Wall Damp Treatment & Painting"
                "Cleaner" -> "Deep Home Sanitization & Scrubbing"
                "Caregiver" -> "Assisted Elder Care & Vitals Check"
                "Driver" -> "On-Demand Chauffeur Transit"
                "Gardener" -> "Lawn Mowing & Shrub Maintenance"
                "Technician" -> "Appliance Diagnosis & Servicing"
                else -> "Standard Cooperative Service Visit"
            }
        )
    }
    var description by remember { mutableStateOf("Please arrive with standard diagnostic toolkit. Issue located on ground floor.") }
    var selectedDate by remember { mutableStateOf("Today") }
    var selectedSlot by remember { mutableStateOf("11:00 AM - 01:00 PM") }
    var customerName by remember { mutableStateOf("Pooja Sharma") }
    var customerPhone by remember { mutableStateOf("+91 98112 34567") }
    var customerAddress by remember { mutableStateOf("Flat 204, Block C, Mayur Vihar Phase 1, Delhi") }
    var paymentMethod by remember { mutableStateOf("UPI / Jan Dhan") }

    val baseRate = worker.hourlyRate
    val welfareFee = (baseRate * 0.03).toInt()
    val totalAmount = baseRate

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
                .widthIn(max = 600.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Book Cooperative Artisan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CooperativeNavy
                    )
                    Text(
                        text = "${worker.name} • ${worker.trade}",
                        fontSize = 13.sp,
                        color = SaffronTrust,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cooperative Guarantee Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(WelfareGreenLight)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Handshake,
                        contentDescription = "Fair Model",
                        tint = WelfareGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "100% Cooperative Owned Guarantee",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = WelfareGreen
                        )
                        Text(
                            text = "Zero corporate commissions. 97% goes directly to artisan, 3% to worker social security & toolkit fund.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Service details input
            Text(
                text = "Service Required",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = serviceTitle,
                onValueChange = { serviceTitle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_service_title_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Problem Description / Instructions",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_description_input"),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Time selection
            Text(
                text = "Preferred Schedule",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Today", "Tomorrow", "Weekend").forEach { d ->
                    val isSelected = selectedDate == d
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SaffronTrust else Color(0xFFF1F5F9))
                            .clickable { selectedDate = d }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = d,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("09:00 - 11:00 AM", "11:00 AM - 01:00 PM", "03:00 - 05:00 PM").forEach { slot ->
                    val isSelected = selectedSlot == slot
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CooperativeNavy else Color(0xFFF1F5F9))
                            .clickable { selectedSlot = slot }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slot,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Customer Contact & Address
            Text(
                text = "Service Address & Contact",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = customerAddress,
                onValueChange = { customerAddress = it },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = SaffronTrust) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_address_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CooperativeNavy) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("booking_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CooperativeNavy) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("booking_phone_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transparent Invoice Breakdown Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Transparent Cooperative Pricing",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CooperativeNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Standard Artisan Labour", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "₹$baseRate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Artisan Welfare Fund (3%)", fontSize = 12.sp, color = WelfareGreen)
                        Text(text = "₹$welfareFee (included)", fontSize = 12.sp, color = WelfareGreen, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Platform Aggregator Commission", fontSize = 12.sp, color = TextMuted)
                        Row {
                            Text(
                                text = "₹120",
                                fontSize = 11.sp,
                                color = TextMuted,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "₹0 (Coop Free)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WelfareGreen)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = SurfaceBorder)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Estimated Total", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text(
                            text = "₹$totalAmount",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = SaffronTrust
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment method selector
            Text(
                text = "Payment Method",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("UPI / Jan Dhan", "Cash after Job").forEach { method ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { paymentMethod = method }
                            .padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = SaffronTrust)
                        )
                        Text(text = method, fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm Button
            Button(
                onClick = {
                    onConfirmBooking(
                        serviceTitle,
                        description,
                        selectedDate,
                        selectedSlot,
                        customerName,
                        customerPhone,
                        customerAddress,
                        totalAmount,
                        paymentMethod
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_booking_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm Booking • ₹$totalAmount",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
