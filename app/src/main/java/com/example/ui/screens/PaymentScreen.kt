package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEntity
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Payment method selection options
 */
enum class PaymentOption(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector
) {
    UPI(
        title = "UPI Instant Transfer",
        subtitle = "GPay, PhonePe, Paytm, or BHIM Jan Dhan",
        badge = "0% Aggregator Fee",
        icon = Icons.Default.Payments
    ),
    CARD(
        title = "Debit / Credit Card",
        subtitle = "RuPay, Visa, MasterCard (Cooperative Gateway)",
        badge = "Secure 256-bit",
        icon = Icons.Default.CreditCard
    ),
    CASH(
        title = "Cash on Service Handover",
        subtitle = "Direct physical cash payment to the verified artisan",
        badge = "Physical Receipt",
        icon = Icons.Default.LocalAtm
    )
}

/**
 * Demo Payment Screen with UPI, Card, and Cash options,
 * followed by a full digital cooperative tax & welfare invoice generation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    booking: BookingEntity,
    onPaymentSuccess: (method: String, invoiceId: String, rating: Float, review: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Payment state
    var selectedMethod by remember { mutableStateOf(PaymentOption.UPI) }
    var upiAppSelected by remember { mutableStateOf("GPay") }
    var customVpa by remember { mutableStateOf("customer@oksbi") }

    // Card state
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8921") }
    var cardExpiry by remember { mutableStateOf("08/29") }
    var cardCvv by remember { mutableStateOf("•••") }
    var cardHolder by remember { mutableStateOf(booking.customerName.ifBlank { "Ananya Sen" }) }

    // Cash state
    var cashHandoverConfirmed by remember { mutableStateOf(false) }

    // Artisan rating & tip
    var tipAmount by remember { mutableStateOf(0) }
    var ratingScore by remember { mutableStateOf(5f) }
    var reviewNote by remember { mutableStateOf("Professional workmanship, on-time arrival, and no hidden charges!") }

    // Processing & Invoice State
    var isProcessingPayment by remember { mutableStateOf(false) }
    var showInvoiceView by remember { mutableStateOf(false) }
    var generatedInvoiceId by remember {
        val invoicePrefix = "SHRAM-INV-"
        val randomSuffix = (100000..999999).random()
        mutableStateOf("$invoicePrefix$randomSuffix")
    }

    val baseAmount = booking.totalAmount
    val grandTotal = baseAmount + tipAmount
    val welfareFundShare = ((grandTotal * 0.03).toInt()).coerceAtLeast(10)
    val workerEarningShare = grandTotal - welfareFundShare

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (showInvoiceView) "Digital Cooperative Invoice" else "Cooperative Payment Settlement",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CooperativeNavy
                        )
                        Text(
                            text = if (showInvoiceView) "GST & Welfare Compliant Bill" else "Order #${booking.id} • ${booking.workerName}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (showInvoiceView) {
                                // Close all and notify success
                                onPaymentSuccess(
                                    selectedMethod.title,
                                    generatedInvoiceId,
                                    ratingScore,
                                    reviewNote
                                )
                            } else {
                                onClose()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CooperativeNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (showInvoiceView) {
                // Generated Digital Invoice view
                DigitalInvoiceScreen(
                    booking = booking,
                    invoiceId = generatedInvoiceId,
                    paymentMethod = selectedMethod.title,
                    totalPaid = grandTotal,
                    tipPaid = tipAmount,
                    workerShare = workerEarningShare,
                    welfareFee = welfareFundShare,
                    ratingGiven = ratingScore,
                    reviewGiven = reviewNote,
                    onDone = {
                        onPaymentSuccess(
                            selectedMethod.title,
                            generatedInvoiceId,
                            ratingScore,
                            reviewNote
                        )
                    }
                )
            } else {
                // Payment Selection & Confirmation Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .widthIn(max = 600.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Summary Card of Work Done
                    ServiceSummaryCard(
                        booking = booking,
                        baseAmount = baseAmount,
                        tipAmount = tipAmount,
                        grandTotal = grandTotal
                    )

                    // Tip the Artisan Pill selector
                    ArtisanTipSection(
                        selectedTip = tipAmount,
                        onSelectTip = { tipAmount = it }
                    )

                    // Payment Methods Section
                    Text(
                        text = "Choose Payment Option",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CooperativeNavy
                    )

                    // Options: UPI, Card, Cash
                    PaymentOption.values().forEach { option ->
                        PaymentOptionCard(
                            option = option,
                            isSelected = selectedMethod == option,
                            onClick = { selectedMethod = option }
                        )
                    }

                    // Detailed Form Based on Selected Method
                    AnimatedVisibility(visible = selectedMethod == PaymentOption.UPI) {
                        UpiPaymentDetailsCard(
                            selectedApp = upiAppSelected,
                            onAppSelect = { upiAppSelected = it },
                            vpa = customVpa,
                            onVpaChange = { customVpa = it },
                            workerName = booking.workerName,
                            amount = grandTotal
                        )
                    }

                    AnimatedVisibility(visible = selectedMethod == PaymentOption.CARD) {
                        CardPaymentDetailsCard(
                            cardNumber = cardNumber,
                            onCardNumberChange = { cardNumber = it },
                            expiry = cardExpiry,
                            onExpiryChange = { cardExpiry = it },
                            cvv = cardCvv,
                            onCvvChange = { cardCvv = it },
                            holderName = cardHolder,
                            onHolderChange = { cardHolder = it }
                        )
                    }

                    AnimatedVisibility(visible = selectedMethod == PaymentOption.CASH) {
                        CashPaymentDetailsCard(
                            workerName = booking.workerName,
                            amount = grandTotal,
                            isConfirmed = cashHandoverConfirmed,
                            onToggleConfirm = { cashHandoverConfirmed = it }
                        )
                    }

                    // Rating & Feedback section
                    ArtisanRatingFeedbackCard(
                        workerName = booking.workerName,
                        rating = ratingScore,
                        onRatingChanged = { ratingScore = it },
                        review = reviewNote,
                        onReviewChanged = { reviewNote = it }
                    )

                    // Cooperative Trust & Zero Cut Guarantee
                    CooperativeZeroCutGuaranteeCard()

                    Spacer(modifier = Modifier.height(6.dp))

                    // Final Pay & Generate Invoice Button
                    Button(
                        onClick = {
                            isProcessingPayment = true
                            coroutineScope.launch {
                                // Simulate ultra-fast cooperative payment settlement
                                delay(1200)
                                isProcessingPayment = false
                                showInvoiceView = true
                            }
                        },
                        enabled = !isProcessingPayment && (selectedMethod != PaymentOption.CASH || cashHandoverConfirmed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_payment_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WelfareGreen,
                            disabledContainerColor = WelfareGreen.copy(alpha = 0.5f)
                        )
                    ) {
                        if (isProcessingPayment) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Settling with Cooperative...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pay ₹$grandTotal & Generate Invoice",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT: SERVICE SUMMARY CARD
// -----------------------------------------------------------------------------
@Composable
fun ServiceSummaryCard(
    booking: BookingEntity,
    baseAmount: Int,
    tipAmount: Int,
    grandTotal: Int
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = booking.serviceTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CooperativeNavy
                    )
                    Text(
                        text = "Artisan: ${booking.workerName} • ${booking.trade}",
                        fontSize = 12.sp,
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
                        text = "Job Complete",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WelfareGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Standard Labour Charges", fontSize = 12.sp, color = TextSecondary)
                Text(text = "₹$baseAmount", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }

            if (tipAmount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Artisan Appreciation Tip", fontSize = 12.sp, color = SaffronTrust)
                    Text(text = "+₹$tipAmount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronTrust)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Corporate Aggregator Platform Cut", fontSize = 12.sp, color = TextMuted)
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
                Text(text = "Net Amount Payable", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CooperativeNavy)
                Text(
                    text = "₹$grandTotal",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                    color = SaffronTrust
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT: ARTISAN TIP SECTION
// -----------------------------------------------------------------------------
@Composable
fun ArtisanTipSection(
    selectedTip: Int,
    onSelectTip: (Int) -> Unit
) {
    val tipOptions = listOf(0, 30, 50, 100)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tip Artisan Directly (100% to Worker)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CooperativeNavy
            )
            if (selectedTip > 0) {
                Text(
                    text = "₹$selectedTip added",
                    fontSize = 11.sp,
                    color = WelfareGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tipOptions.forEach { tip ->
                val isSelected = selectedTip == tip
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SaffronTrust else Color.White)
                        .border(
                            1.dp,
                            if (isSelected) SaffronTrust else SurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectTip(tip) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (tip == 0) "No Tip" else "+₹$tip",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT: PAYMENT OPTION CARD
// -----------------------------------------------------------------------------
@Composable
fun PaymentOptionCard(
    option: PaymentOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFFBEB) else Color.White
        ),
        border = BorderStroke(
            if (isSelected) 1.8.dp else 1.dp,
            if (isSelected) SaffronTrust else SurfaceBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("payment_option_${option.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = SaffronTrust)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SaffronTrust.copy(alpha = 0.15f) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.title,
                    tint = if (isSelected) SaffronTrust else CooperativeNavy,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = option.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CooperativeNavy
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WelfareGreenLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = option.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = WelfareGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = option.subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT: UPI PAYMENT DETAILS CARD
// -----------------------------------------------------------------------------
@Composable
fun UpiPaymentDetailsCard(
    selectedApp: String,
    onAppSelect: (String) -> Unit,
    vpa: String,
    onVpaChange: (String) -> Unit,
    workerName: String,
    amount: Int
) {
    val upiApps = listOf("GPay", "PhonePe", "Paytm", "BHIM / Jan Dhan")

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Select UPI Application or Scan QR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CooperativeNavy
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                upiApps.forEach { app ->
                    val isAppSelected = selectedApp == app
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAppSelected) CooperativeNavy else Color(0xFFF1F5F9))
                            .clickable { onAppSelect(app) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app,
                            fontSize = 11.sp,
                            fontWeight = if (isAppSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAppSelected) Color.White else TextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Enter VPA or UPI ID
            OutlinedTextField(
                value = vpa,
                onValueChange = onVpaChange,
                label = { Text("Customer UPI ID / Virtual Address", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronTrust,
                    unfocusedBorderColor = SurfaceBorder
                ),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = SaffronTrust,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = WelfareGreen,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Direct NPCI routing to Artisan's Jan Dhan account (No middleman retention)",
                    fontSize = 10.sp,
                    color = WelfareGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT: CARD PAYMENT DETAILS CARD
// -----------------------------------------------------------------------------
@Composable
fun CardPaymentDetailsCard(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    expiry: String,
    onExpiryChange: (String) -> Unit,
    cvv: String,
    onCvvChange: (String) -> Unit,
    holderName: String,
    onHolderChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Enter Card Information",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CooperativeNavy
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                label = { Text("Card Number", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronTrust,
                    unfocusedBorderColor = SurfaceBorder
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = CooperativeNavy, modifier = Modifier.size(18.dp))
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = expiry,
                    onValueChange = onExpiryChange,
                    label = { Text("MM/YY", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronTrust,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = onCvvChange,
                    label = { Text("CVV", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronTrust,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = holderName,
                onValueChange = onHolderChange,
                label = { Text("Cardholder Name", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronTrust,
                    unfocusedBorderColor = SurfaceBorder
                ),
                singleLine = true
            )
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT: CASH PAYMENT DETAILS CARD
// -----------------------------------------------------------------------------
@Composable
fun CashPaymentDetailsCard(
    workerName: String,
    amount: Int,
    isConfirmed: Boolean,
    onToggleConfirm: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalAtm,
                    contentDescription = null,
                    tint = WelfareGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cash Handover Protocol",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = WelfareGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Please hand over exact cash amount of ₹$amount to artisan $workerName in person. A digital tax receipt and warranty invoice will be generated instantly.",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(8.dp))
                    .clickable { onToggleConfirm(!isConfirmed) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isConfirmed) WelfareGreen else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isConfirmed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirmed",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "I confirm cash payment handover to $workerName",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CooperativeNavy
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT: ARTISAN RATING & FEEDBACK CARD
// -----------------------------------------------------------------------------
@Composable
fun ArtisanRatingFeedbackCard(
    workerName: String,
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    review: String,
    onReviewChanged: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Rate Artisan Experience",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CooperativeNavy
            )
            Text(
                text = "Your rating directly strengthens $workerName's cooperative equity standing.",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..5).forEach { star ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "$star Stars",
                        tint = if (star <= rating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onRatingChanged(star.toFloat()) }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$rating / 5.0",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CooperativeNavy
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = review,
                onValueChange = onReviewChanged,
                label = { Text("Share feedback for the cooperative guild", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronTrust,
                    unfocusedBorderColor = SurfaceBorder
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT: ZERO CUT GUARANTEE CARD
// -----------------------------------------------------------------------------
@Composable
fun CooperativeZeroCutGuaranteeCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color(0xFF1D4ED8),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Cooperative Fair Value Promise",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF1E3A8A)
                )
                Text(
                    text = "97% goes directly to the worker + 3% to social health safety net. 0% lost to commercial commission.",
                    fontSize = 10.sp,
                    color = Color(0xFF1E40AF),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SCREEN: DIGITAL INVOICE GENERATED
// -----------------------------------------------------------------------------
@Composable
fun DigitalInvoiceScreen(
    booking: BookingEntity,
    invoiceId: String,
    paymentMethod: String,
    totalPaid: Int,
    tipPaid: Int,
    workerShare: Int,
    welfareFee: Int,
    ratingGiven: Float,
    reviewGiven: String,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val currentDateStr = remember {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .widthIn(max = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Success Header Badge
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(WelfareGreenLight)
                .border(2.dp, WelfareGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = WelfareGreen,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Payment Successfully Completed!",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CooperativeNavy,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Funds settled directly to ${booking.workerName}'s cooperative account.",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        // Main Digital Invoice Receipt Card (Tax & Cooperative Breakdown)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, SurfaceBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Cooperative Guild Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "GHARgo Home Services Network",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = CooperativeNavy
                        )
                        Text(
                            text = "Reg No: DL-SHRAM-COOP-2024/889",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "GSTIN: 07AAAAS0000A1Z5",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ORIGINAL TAX INVOICE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Invoice metadata details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "INVOICE NUMBER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(text = invoiceId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CooperativeNavy)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "DATE & TIME", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(text = currentDateStr, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "BILLED TO (CUSTOMER)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(text = booking.customerName.ifBlank { "Ananya Sen" }, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = booking.customerPhone, fontSize = 10.sp, color = TextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "SERVICED BY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(text = booking.workerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronTrust)
                        Text(text = "${booking.trade} • e-Shram Verified", fontSize = 10.sp, color = WelfareGreen)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Line Items Table
                Text(
                    text = "ITEMIZED SERVICE BREAKDOWN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CooperativeNavy
                )
                Spacer(modifier = Modifier.height(6.dp))

                InvoiceLineItem(
                    title = booking.serviceTitle,
                    subtitle = "Standard cooperative trade service visit",
                    amount = booking.totalAmount
                )

                if (tipPaid > 0) {
                    InvoiceLineItem(
                        title = "Direct Artisan Appreciation Tip",
                        subtitle = "Voluntary customer bonus (100% to worker)",
                        amount = tipPaid,
                        isHighlight = true
                    )
                }

                InvoiceLineItem(
                    title = "Artisan Welfare Fund (3% Included)",
                    subtitle = "Ayushman health & tool insurance allocation",
                    amount = welfareFee,
                    isDeduction = false,
                    isGreen = true
                )

                InvoiceLineItem(
                    title = "Commercial Brokerage / Aggregator Take",
                    subtitle = "Cooperative model saves customer & worker",
                    amount = 0,
                    isFree = true
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Grand total and payment method
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "TOTAL AMOUNT PAID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(
                            text = "Via $paymentMethod",
                            fontSize = 11.sp,
                            color = WelfareGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "₹$totalPaid",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CooperativeNavy
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Co-op distribution breakdown card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Transparent Payout Summary:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CooperativeNavy
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "• Transferred to ${booking.workerName}", fontSize = 10.sp, color = TextSecondary)
                            Text(text = "₹$workerShare", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CooperativeNavy)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "• Deposited in Sahakari Welfare Reserve", fontSize = 10.sp, color = WelfareGreen)
                            Text(text = "₹$welfareFee", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WelfareGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Customer Rating Recorded
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Verified Review Logged:", fontSize = 11.sp, color = TextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "$ratingGiven★ Rated", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CooperativeNavy)
                    }
                }
                if (reviewGiven.isNotBlank()) {
                    Text(
                        text = "\"$reviewGiven\"",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Action Buttons: Copy Invoice ID, Download/Share, Finish
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Invoice ID", invoiceId)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Invoice ID $invoiceId copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy ID", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    Toast.makeText(context, "Digital PDF Invoice downloaded to storage", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("done_invoice_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Done • Return to Bookings", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InvoiceLineItem(
    title: String,
    subtitle: String,
    amount: Int,
    isHighlight: Boolean = false,
    isDeduction: Boolean = false,
    isGreen: Boolean = false,
    isFree: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighlight) SaffronTrust else if (isGreen) WelfareGreen else TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextMuted
            )
        }

        if (isFree) {
            Text(
                text = "₹0.00 (Waived)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = WelfareGreen
            )
        } else {
            Text(
                text = "${if (isDeduction) "-" else ""}₹$amount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGreen) WelfareGreen else if (isHighlight) SaffronTrust else TextPrimary
            )
        }
    }
}
