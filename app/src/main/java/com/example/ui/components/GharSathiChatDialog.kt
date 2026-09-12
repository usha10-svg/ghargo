package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GharSathiActionType
import com.example.ai.GharSathiMessage
import com.example.ai.GharSathiService
import com.example.data.BookingEntity
import com.example.data.WorkerEntity
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WelfareGreen
import com.example.viewmodel.RateCardItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GharSathiChatDialog(
    onDismiss: () -> Unit,
    allWorkers: List<WorkerEntity>,
    allBookings: List<BookingEntity>,
    rateCards: List<RateCardItem>,
    currentUserPhone: String,
    onBookWorker: (WorkerEntity) -> Unit,
    onViewWorker: (WorkerEntity) -> Unit,
    onNavigateToServices: (String) -> Unit,
    onNavigateToBookings: () -> Unit,
    onOpenRateCard: () -> Unit,
    onOpenEmergency: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val chatService = remember { GharSathiService() }

    // Language State: true = Hindi, false = English
    var isHindi by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    // Initial Welcome Message
    val messages = remember {
        mutableStateListOf(
            GharSathiMessage(
                text = "👋 Namaste! I am **gharSathi**, your intelligent ShramConnect companion.\n\nI can help you search verified cooperative technicians, track bookings, understand transparent 0% markup pricing, or get emergency help. Feel free to switch between **हिंदी** and **English** anytime!",
                isUser = false,
                language = "en",
                actionType = GharSathiActionType.GENERAL_CHAT
            )
        )
    }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessage(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank() || isThinking) return

        // Auto-detect Hindi characters in user query to auto-switch if needed
        val hasHindiChars = trimmed.any { it in '\u0900'..'\u097F' }
        if (hasHindiChars && !isHindi) {
            isHindi = true
        }

        messages.add(
            GharSathiMessage(
                text = trimmed,
                isUser = true,
                language = if (isHindi) "hi" else "en"
            )
        )
        inputText = ""
        isThinking = true

        scope.launch {
            val response = chatService.getResponse(
                userInput = trimmed,
                isHindi = isHindi,
                allWorkers = allWorkers,
                allBookings = allBookings,
                rateCards = rateCards,
                currentUserPhone = currentUserPhone
            )
            isThinking = false
            messages.add(response)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color(0xFFF8FAFC),
        modifier = Modifier
            .fillMaxSize()
            .testTag("gharsathi_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                CooperativeNavy,
                                Color(0xFF1E3A8A),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Robot Avatar & Identity
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SaffronTrust,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "gharSathi AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "घरसाथी AI" else "gharSathi AI",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = WelfareGreen.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, WelfareGreen.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = if (isHindi) "सक्रिय" else "Online",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WelfareGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isHindi) "श्रमकनेक्ट सहकारी सहायक" else "ShramConnect Cooperative Assistant",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    // Language Toggle & Close Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Language Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clickable {
                                    isHindi = !isHindi
                                    // Add language transition announcement
                                    messages.add(
                                        GharSathiMessage(
                                            text = if (isHindi) {
                                                "🇮🇳 **भाषा हिंदी में बदल दी गई है।** अब मैं आपसे हिंदी में संवाद करूँगा।"
                                            } else {
                                                "🇬🇧 **Language switched to English.** I will now assist you in English."
                                            },
                                            isUser = false,
                                            language = if (isHindi) "hi" else "en"
                                        )
                                    )
                                }
                                .testTag("gharsathi_language_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Switch Language",
                                    tint = SaffronTrust,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "हिंदी ▾" else "English ▾",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp).testTag("gharsathi_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Chat",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Quick Prompt Suggestion Chips (Horizontal Carousel)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
            ) {
                val suggestions = if (isHindi) {
                    listOf(
                        "⚡ इलेक्ट्रीशियन चाहिए",
                        "🔧 प्लम्बर खोजें",
                        "📋 मेरी बुकिंग का स्टेटस",
                        "💰 पारदर्शी रेट कार्ड",
                        "🤝 सहकारी मॉडल क्या है?",
                        "🚨 इमरजेंसी रिपेयर"
                    )
                } else {
                    listOf(
                        "⚡ Need an Electrician",
                        "🔧 Find a Plumber",
                        "📋 Check Booking Status",
                        "💰 Transparent Rate Card",
                        "🤝 What is Cooperative?",
                        "🚨 24/7 Emergency SOS"
                    )
                }

                items(suggestions) { chipText ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp,
                        modifier = Modifier.clickable { sendMessage(chipText) }
                    ) {
                        Text(
                            text = chipText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CooperativeNavy,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Chat Messages Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        isHindi = isHindi,
                        onBookWorker = onBookWorker,
                        onViewWorker = onViewWorker,
                        onNavigateToServices = onNavigateToServices,
                        onNavigateToBookings = onNavigateToBookings,
                        onOpenRateCard = onOpenRateCard,
                        onOpenEmergency = onOpenEmergency
                    )
                }

                // Thinking / Generating Indicator
                if (isThinking) {
                    item {
                        ThinkingBubble(isHindi = isHindi)
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("gharsathi_input_field"),
                        placeholder = {
                            Text(
                                text = if (isHindi) "घरसाथी से कुछ भी पूछें..." else "Ask gharSathi anything...",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronTrust,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage(inputText) })
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = if (inputText.isNotBlank() && !isThinking) SaffronTrust else Color(0xFFCBD5E1),
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .clickable(enabled = inputText.isNotBlank() && !isThinking) {
                                sendMessage(inputText)
                            }
                            .testTag("gharsathi_send_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: GharSathiMessage,
    isHindi: Boolean,
    onBookWorker: (WorkerEntity) -> Unit,
    onViewWorker: (WorkerEntity) -> Unit,
    onNavigateToServices: (String) -> Unit,
    onNavigateToBookings: () -> Unit,
    onOpenRateCard: () -> Unit,
    onOpenEmergency: () -> Unit
) {
    if (message.isUser) {
        // User Message (Right-aligned)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                color = CooperativeNavy,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    } else {
        // AI gharSathi Message (Left-aligned)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Surface(
                shape = CircleShape,
                color = SaffronTrust,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Top)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.fillMaxWidth(0.92f)) {
                Surface(
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = message.text,
                            fontSize = 13.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 18.sp
                        )

                        // Action cards attached to the message
                        when (message.actionType) {
                            GharSathiActionType.RECOMMENDED_WORKERS -> {
                                if (message.recommendedWorkers.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (isHindi) "सत्यापित कारीगर कार्ड्स:" else "Verified Artisan Cards:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CooperativeNavy
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    message.recommendedWorkers.forEach { worker ->
                                        WorkerMiniCard(
                                            worker = worker,
                                            isHindi = isHindi,
                                            onBook = { onBookWorker(worker) },
                                            onView = { onViewWorker(worker) }
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = { onNavigateToServices(message.targetTrade ?: "ALL") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CooperativeNavy)
                                    ) {
                                        Text(
                                            text = if (isHindi) "मार्केटप्लेस में सभी देखें →" else "View All in Marketplace →",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            GharSathiActionType.BOOKING_STATUS -> {
                                if (message.relatedBookings.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    message.relatedBookings.forEach { booking ->
                                        BookingMiniCard(booking = booking, isHindi = isHindi)
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                    Button(
                                        onClick = onNavigateToBookings,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isHindi) "बुकिंग्स पेज पर ट्रैक करें" else "Track in Bookings Tab",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onNavigateToServices("ALL") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CooperativeNavy)
                                    ) {
                                        Text(
                                            text = if (isHindi) "कारीगर खोजें & बुक करें" else "Find & Book a Worker",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            GharSathiActionType.RATE_CARD -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                RateCardComparisonSnippet(isHindi = isHindi)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onOpenRateCard,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CooperativeNavy)
                                ) {
                                    Text(
                                        text = if (isHindi) "पूरा सहकारी रेट कार्ड खोलें 📋" else "Open Full Rate Card 📋",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            GharSathiActionType.EMERGENCY_DISPATCH -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = onOpenEmergency,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "24/7 आपातकालीन डिस्पैच खोलें 🚨" else "Open 24/7 Emergency Dispatch 🚨",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            GharSathiActionType.COOPERATIVE_TRANSPARENCY -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                CoopHighlightPills(isHindi = isHindi)
                            }

                            GharSathiActionType.GENERAL_CHAT -> {
                                // Standard conversational answer
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerMiniCard(
    worker: WorkerEntity,
    isHindi: Boolean,
    onBook: () -> Unit,
    onView: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CooperativeNavy,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = worker.name.take(1),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = worker.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = SaffronTrust,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = "${worker.trade} • ${worker.experienceYears}y exp",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${worker.hourlyRate}/hr",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SaffronTrust
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "${worker.rating}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (isHindi) "प्रोफाइल" else "Profile",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CooperativeNavy
                    )
                }

                Button(
                    onClick = onBook,
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (isHindi) "बुक करें" else "Book Now",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingMiniCard(
    booking: BookingEntity,
    isHindi: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF0FDF4),
        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.serviceTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                ) {
                    Text(
                        text = booking.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Technician: ${booking.workerName} (${booking.trade})",
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )
            Text(
                text = "Time: ${booking.scheduledDate} • ${booking.scheduledTime}",
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // OTP Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "कार्य शुरू करने का OTP:" else "Job Start Security OTP:",
                            fontSize = 10.sp,
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = booking.otpCode,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF15803D)
                    )
                }
            }
        }
    }
}

@Composable
private fun RateCardComparisonSnippet(isHindi: Boolean) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFEF3C7).copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color(0xFFFDE68A))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = if (isHindi) "सहकारी बनाम निजी ऐप्स बचत तुलना" else "Coop vs Private Apps Savings",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E)
            )
            Spacer(modifier = Modifier.height(6.dp))

            RateRow("Switchboard Fix", "₹149", "₹299", "Save 50%")
            Spacer(modifier = Modifier.height(4.dp))
            RateRow("Tap Leak Repair", "₹200", "₹380", "Save 47%")
            Spacer(modifier = Modifier.height(4.dp))
            RateRow("AC Jet Foam Service", "₹499", "₹899", "Save 45%")
        }
    }
}

@Composable
private fun RateRow(service: String, coop: String, market: String, save: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = service, fontSize = 11.sp, color = Color(0xFF1F2937), modifier = Modifier.weight(1.2f))
        Text(text = "Coop: $coop", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronTrust)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = market, fontSize = 10.sp, color = Color(0xFF9CA3AF))
        Spacer(modifier = Modifier.width(6.dp))
        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFDCFCE7)) {
            Text(text = save, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
        }
    }
}

@Composable
private fun CoopHighlightPills(isHindi: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        HighlightPill(
            icon = "🗳️",
            title = if (isHindi) "1 कारीगर = 1 वोट" else "1 Worker = 1 Vote",
            desc = if (isHindi) "सभी नीतियां आम सभा में तय होती हैं" else "Democratic cooperative governance"
        )
        HighlightPill(
            icon = "🏥",
            title = if (isHindi) "3% वेलफेयर सेस" else "3% Health & Accident Pool",
            desc = if (isHindi) "कारीगरों के इलाज और सुरक्षा के लिए" else "Medical security for technician families"
        )
        HighlightPill(
            icon = "💎",
            title = if (isHindi) "0% निजी कॉर्पोरेट कट" else "0% Middlemen Commission",
            desc = if (isHindi) "97% कमाई सीधे कारीगर की जेब में" else "97% earnings retained by verified artisans"
        )
    }
}

@Composable
private fun HighlightPill(icon: String, title: String, desc: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFDBEAFE))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CooperativeNavy)
                Text(text = desc, fontSize = 10.sp, color = Color(0xFF475569))
            }
        }
    }
}

@Composable
private fun ThinkingBubble(isHindi: Boolean) {
    val transition = rememberInfiniteTransition(label = "thinking")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = SaffronTrust,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SaffronTrust.copy(alpha = alpha),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "घरसाथी सोच रहा है..." else "gharSathi is thinking...",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
