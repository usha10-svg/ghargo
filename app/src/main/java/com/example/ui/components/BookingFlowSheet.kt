package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
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

data class ServiceOption(
    val trade: String,
    val title: String,
    val description: String,
    val basePrice: Int,
    val durationText: String
)

val defaultServicesCatalog = listOf(
    ServiceOption(
        trade = "Electrician",
        title = "Electrical Inspection & Wiring Fix",
        description = "Circuit fault tracing, switchboard repairs, and MCB protection check.",
        basePrice = 249,
        durationText = "60-90 min"
    ),
    ServiceOption(
        trade = "Electrician",
        title = "Ceiling Fan / Lighting Installation",
        description = "Safe installation and regulator testing with earth fault inspection.",
        basePrice = 199,
        durationText = "45 min"
    ),
    ServiceOption(
        trade = "Plumber",
        title = "Sanitary Fixture & Tap Leak Repair",
        description = "Precision washers, tap replacement, and pipe joint sealing.",
        basePrice = 199,
        durationText = "45-60 min"
    ),
    ServiceOption(
        trade = "Plumber",
        title = "Water Tank & Main Line Cleansing",
        description = "High-pressure hygiene flush and valve inspection.",
        basePrice = 399,
        durationText = "90 min"
    ),
    ServiceOption(
        trade = "Carpenter",
        title = "Furniture & Door Lock Restoration",
        description = "Hinge adjustment, cylindrical lock fitting, and hardwood smoothing.",
        basePrice = 299,
        durationText = "60 min"
    ),
    ServiceOption(
        trade = "Painter",
        title = "Wall Damp Proofing & Patch Painting",
        description = "Waterproofing primer application and color matching finish.",
        basePrice = 349,
        durationText = "2-3 hrs"
    ),
    ServiceOption(
        trade = "Cleaner",
        title = "Deep Kitchen & Bathroom Sanitization",
        description = "Tile de-scaling, chrome polish, and floor scrubbing with eco-solvents.",
        basePrice = 299,
        durationText = "2 hrs"
    ),
    ServiceOption(
        trade = "Caregiver",
        title = "Senior Home Assistance & Vitals",
        description = "Compassionate vital monitoring, mobility help, and medicine tracking.",
        basePrice = 399,
        durationText = "Half Day"
    ),
    ServiceOption(
        trade = "Technician",
        title = "AC / Refrigerator General Service",
        description = "Cooling coil cleanse, gas pressure check, and amperage tuning.",
        basePrice = 349,
        durationText = "60 min"
    )
)

/**
 * 4-Step Simple Booking Flow:
 * Step 1: Select Service
 * Step 2: Select Worker (Artisan from cooperative)
 * Step 3: Select Date & Time (Slot & Customer Address)
 * Step 4: Confirm Booking (Transparent bill, 0% commission, OTP generated)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFlowSheet(
    allWorkers: List<WorkerEntity>,
    initialWorker: WorkerEntity? = null,
    initialServiceTitle: String? = null,
    initialTrade: String? = null,
    onDismiss: () -> Unit,
    onConfirmBooking: (
        worker: WorkerEntity,
        serviceTitle: String,
        description: String,
        date: String,
        timeSlot: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        totalAmount: Int,
        paymentMethod: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Current Step: 1 = Service, 2 = Worker, 3 = Date/Time, 4 = Confirm
    var currentStep by remember {
        mutableIntStateOf(if (initialWorker != null) 2 else 1)
    }

    // Step 1 State: Service
    var selectedTrade by remember {
        mutableStateOf(initialWorker?.trade ?: initialTrade ?: "Electrician")
    }
    var selectedService by remember {
        mutableStateOf(
            defaultServicesCatalog.firstOrNull { it.trade.equals(selectedTrade, ignoreCase = true) }
                ?: defaultServicesCatalog.first()
        )
    }
    var customServiceNotes by remember {
        mutableStateOf("Please bring standard diagnostic toolkit.")
    }

    // Step 2 State: Worker
    val availableWorkersForTrade = remember(selectedTrade, allWorkers) {
        val filtered = allWorkers.filter { it.trade.equals(selectedTrade, ignoreCase = true) }
        if (filtered.isNotEmpty()) filtered else allWorkers
    }
    var selectedWorker by remember {
        mutableStateOf(initialWorker ?: availableWorkersForTrade.firstOrNull() ?: allWorkers.first())
    }

    // Step 3 State: Date / Time & Customer Info
    var selectedDate by remember { mutableStateOf("Today, 11 Sep") }
    var selectedTimeSlot by remember { mutableStateOf("02:00 PM - 04:00 PM") }
    var customerName by remember { mutableStateOf("Pooja Sharma") }
    var customerPhone by remember { mutableStateOf("+91 98112 34567") }
    var customerAddress by remember { mutableStateOf("Flat 204, Block C, Mayur Vihar Phase 1, Delhi") }
    var paymentMethod by remember { mutableStateOf("UPI / Jan Dhan") }

    val baseLaborRate = selectedWorker.hourlyRate
    val welfareFund = (baseLaborRate * 0.03).toInt().coerceAtLeast(10)
    val totalAmount = baseLaborRate

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
                .padding(bottom = 32.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (currentStep > 1) {
                        IconButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CooperativeNavy
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Column {
                        Text(
                            text = "Book Artisan Service",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CooperativeNavy
                        )
                        Text(
                            text = "Delhi Shramik Sahakari • 100% Cooperative Owned",
                            fontSize = 11.sp,
                            color = SaffronTrust,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Indicator Strip
            BookingStepProgressBar(currentStep = currentStep)

            Spacer(modifier = Modifier.height(18.dp))

            // Step Content
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "BookingStepAnimation"
            ) { step ->
                when (step) {
                    1 -> Step1SelectService(
                        selectedTrade = selectedTrade,
                        selectedService = selectedService,
                        customNotes = customServiceNotes,
                        onTradeSelected = { trade ->
                            selectedTrade = trade
                            val match = defaultServicesCatalog.firstOrNull { it.trade.equals(trade, ignoreCase = true) }
                            if (match != null) selectedService = match
                            val workers = allWorkers.filter { it.trade.equals(trade, ignoreCase = true) }
                            if (workers.isNotEmpty()) selectedWorker = workers.first()
                        },
                        onServiceSelected = { service -> selectedService = service },
                        onNotesChanged = { customServiceNotes = it },
                        onNext = { currentStep = 2 }
                    )

                    2 -> Step2SelectWorker(
                        workers = availableWorkersForTrade,
                        selectedWorker = selectedWorker,
                        onWorkerSelected = { worker -> selectedWorker = worker },
                        onNext = { currentStep = 3 }
                    )

                    3 -> Step3SelectDateTime(
                        selectedDate = selectedDate,
                        selectedTimeSlot = selectedTimeSlot,
                        customerName = customerName,
                        customerPhone = customerPhone,
                        customerAddress = customerAddress,
                        onDateSelected = { selectedDate = it },
                        onTimeSlotSelected = { selectedTimeSlot = it },
                        onNameChanged = { customerName = it },
                        onPhoneChanged = { customerPhone = it },
                        onAddressChanged = { customerAddress = it },
                        onNext = { currentStep = 4 }
                    )

                    4 -> Step4ConfirmBooking(
                        worker = selectedWorker,
                        service = selectedService,
                        customNotes = customServiceNotes,
                        date = selectedDate,
                        timeSlot = selectedTimeSlot,
                        customerName = customerName,
                        customerPhone = customerPhone,
                        customerAddress = customerAddress,
                        baseRate = baseLaborRate,
                        welfareFee = welfareFund,
                        totalAmount = totalAmount,
                        paymentMethod = paymentMethod,
                        onPaymentMethodChanged = { paymentMethod = it },
                        onConfirm = {
                            onConfirmBooking(
                                selectedWorker,
                                selectedService.title,
                                customServiceNotes,
                                selectedDate,
                                selectedTimeSlot,
                                customerName,
                                customerPhone,
                                customerAddress,
                                totalAmount,
                                paymentMethod
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Visual Progress Bar showing:
 * 1. Service -> 2. Worker -> 3. Date/Time -> 4. Confirm
 */
@Composable
fun BookingStepProgressBar(currentStep: Int) {
    val steps = listOf("1. Service", "2. Worker", "3. Date & Time", "4. Confirm")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, title ->
                val stepNum = index + 1
                val isDone = currentStep > stepNum
                val isCurrent = currentStep == stepNum

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isDone -> WelfareGreen
                                    isCurrent -> SaffronTrust
                                    else -> Color(0xFFE2E8F0)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        } else {
                            Text(
                                text = "$stepNum",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) Color.White else TextMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title.substringAfter(". "),
                        fontSize = 11.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) CooperativeNavy else if (isDone) WelfareGreen else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFE2E8F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(currentStep / 4f)
                    .height(4.dp)
                    .background(SaffronTrust)
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 1: SELECT SERVICE
// -------------------------------------------------------------
@Composable
fun Step1SelectService(
    selectedTrade: String,
    selectedService: ServiceOption,
    customNotes: String,
    onTradeSelected: (String) -> Unit,
    onServiceSelected: (ServiceOption) -> Unit,
    onNotesChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    val trades = listOf(
        "Electrician", "Plumber", "Carpenter", "Painter",
        "Cleaner", "Caregiver", "Technician"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Step 1 • Select Service Category",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = CooperativeNavy
        )
        Text(
            text = "Choose the professional trade required for your home or office.",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Trade selector horizontal chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            trades.forEach { trade ->
                val isSelected = selectedTrade.equals(trade, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CooperativeNavy else Color(0xFFF1F5F9))
                        .border(
                            1.dp,
                            if (isSelected) CooperativeNavy else SurfaceBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onTradeSelected(trade) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = getTradeIcon(trade),
                            contentDescription = trade,
                            tint = if (isSelected) SaffronTrust else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = trade,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Available Standard Services",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = CooperativeNavy
        )

        Spacer(modifier = Modifier.height(8.dp))

        val servicesInTrade = defaultServicesCatalog.filter {
            it.trade.equals(selectedTrade, ignoreCase = true)
        }.ifEmpty {
            listOf(
                ServiceOption(
                    trade = selectedTrade,
                    title = "Standard $selectedTrade Maintenance Visit",
                    description = "Comprehensive diagnosis and repairs with cooperative-guaranteed rate.",
                    basePrice = 249,
                    durationText = "60 min"
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            servicesInTrade.forEach { service ->
                val isSelected = selectedService.title == service.title
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFFFFBEB) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) SaffronTrust else SurfaceBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onServiceSelected(service) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = service.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CooperativeNavy
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = service.description,
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = WelfareGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Est. ${service.durationText}",
                                    fontSize = 11.sp,
                                    color = WelfareGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${service.basePrice}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SaffronTrust
                            )
                            Text(
                                text = "Base Rate",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Instructions / Problem Details (Optional)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = customNotes,
            onValueChange = onNotesChanged,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("booking_step1_notes_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SaffronTrust,
                unfocusedBorderColor = SurfaceBorder
            ),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("step1_next_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
        ) {
            Text(text = "Next: Select Worker", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 2: SELECT WORKER
// -------------------------------------------------------------
@Composable
fun Step2SelectWorker(
    workers: List<WorkerEntity>,
    selectedWorker: WorkerEntity,
    onWorkerSelected: (WorkerEntity) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Step 2 • Select Worker",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = CooperativeNavy
        )
        Text(
            text = "All artisans are member-owners of the cooperative with verified credentials.",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            workers.forEach { worker ->
                val isSelected = selectedWorker.id == worker.id
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFFFFBEB) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) SaffronTrust else SurfaceBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWorkerSelected(worker) }
                        .testTag("select_worker_${worker.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Worker Photo with Badge
                        WorkerPhotoAvatar(
                            worker = worker,
                            size = 52.dp,
                            showVerificationBadge = true
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = worker.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CooperativeNavy
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${worker.rating}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            // Experience & Jobs
                            Text(
                                text = "${worker.trade} • ${worker.experienceYears} yrs exp • ${worker.completedJobs} jobs",
                                fontSize = 11.sp,
                                color = SaffronTrust,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            // Cooperative Name & Verification
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = CooperativeNavy,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Delhi Shramik Sahakari • ${worker.locality}",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Hourly Rate
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${worker.hourlyRate}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CooperativeNavy
                            )
                            Text(
                                text = "/ hr",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(SaffronTrust),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("step2_next_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
        ) {
            Text(text = "Next: Select Date & Time", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 3: SELECT DATE & TIME
// -------------------------------------------------------------
@Composable
fun Step3SelectDateTime(
    selectedDate: String,
    selectedTimeSlot: String,
    customerName: String,
    customerPhone: String,
    customerAddress: String,
    onDateSelected: (String) -> Unit,
    onTimeSlotSelected: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    val dates = listOf("Today, 11 Sep", "Tomorrow, 12 Sep", "Sat, 13 Sep", "Sun, 14 Sep")
    val timeSlots = listOf(
        "09:00 AM - 11:00 AM",
        "11:00 AM - 01:00 PM",
        "02:00 PM - 04:00 PM",
        "04:00 PM - 06:00 PM",
        "06:00 PM - 08:00 PM"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Step 3 • Select Date & Time",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = CooperativeNavy
        )
        Text(
            text = "Choose your convenient slot for the artisan visit.",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Date selector
        Text(
            text = "Select Service Date",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = CooperativeNavy
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dates.forEach { d ->
                val isSelected = selectedDate == d
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SaffronTrust else Color(0xFFF1F5F9))
                        .border(
                            1.dp,
                            if (isSelected) SaffronTrust else SurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onDateSelected(d) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = d,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time slot selector
        Text(
            text = "Select Preferred Arrival Window",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = CooperativeNavy
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            timeSlots.forEach { slot ->
                val isSelected = selectedTimeSlot == slot
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFFFFFBEB) else Color.White)
                        .border(
                            1.5.dp,
                            if (isSelected) SaffronTrust else SurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onTimeSlotSelected(slot) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isSelected) SaffronTrust else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = slot,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CooperativeNavy else TextPrimary
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SaffronTrust,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Address & contact
        Text(
            text = "Service Address & Contact",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = CooperativeNavy
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = customerAddress,
            onValueChange = onAddressChanged,
            label = { Text("Complete Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SaffronTrust,
                unfocusedBorderColor = SurfaceBorder
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customerName,
                onValueChange = onNameChanged,
                label = { Text("Your Name") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronTrust,
                    unfocusedBorderColor = SurfaceBorder
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = customerPhone,
                onValueChange = onPhoneChanged,
                label = { Text("Phone Number") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronTrust,
                    unfocusedBorderColor = SurfaceBorder
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("step3_next_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
        ) {
            Text(text = "Next: Confirm Booking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 4: CONFIRM BOOKING
// -------------------------------------------------------------
@Composable
fun Step4ConfirmBooking(
    worker: WorkerEntity,
    service: ServiceOption,
    customNotes: String,
    date: String,
    timeSlot: String,
    customerName: String,
    customerPhone: String,
    customerAddress: String,
    baseRate: Int,
    welfareFee: Int,
    totalAmount: Int,
    paymentMethod: String,
    onPaymentMethodChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Step 4 • Confirm Booking",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = CooperativeNavy
        )
        Text(
            text = "Review appointment summary and transparent cooperative pricing.",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Worker & Service Summary Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WorkerPhotoAvatar(
                        worker = worker,
                        size = 46.dp,
                        showVerificationBadge = true
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = worker.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CooperativeNavy
                        )
                        Text(
                            text = "${worker.trade} • Delhi Shramik Sahakari",
                            fontSize = 11.sp,
                            color = SaffronTrust,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WelfareGreenLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "e-Shram Verified",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WelfareGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Service info
                Text(
                    text = "Service: ${service.title}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (customNotes.isNotBlank()) {
                    Text(
                        text = "Notes: $customNotes",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Schedule details
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = SaffronTrust,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$date • $timeSlot",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CooperativeNavy
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = customerAddress,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transparent Rate Card Breakdown
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Cooperative Transparent Pricing",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
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
                    Text(text = "Corporate Aggregator Cut", fontSize = 12.sp, color = TextMuted)
                    Row {
                        Text(
                            text = "₹120",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "₹0 (0% Cut)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WelfareGreen)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total Payable (Post Job)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(
                        text = "₹$totalAmount",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = SaffronTrust
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Mode
        Text(
            text = "Payment Mode",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = CooperativeNavy
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("UPI / Jan Dhan", "Cash after Job").forEach { method ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onPaymentMethodChanged(method) }
                        .padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = paymentMethod == method,
                        onClick = { onPaymentMethodChanged(method) },
                        colors = RadioButtonDefaults.colors(selectedColor = SaffronTrust)
                    )
                    Text(text = method, fontSize = 12.sp, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("confirm_final_booking_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
        ) {
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
