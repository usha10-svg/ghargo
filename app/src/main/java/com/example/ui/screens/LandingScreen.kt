package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LocationSelectorDialog
import com.example.ui.components.TrustDialogType
import com.example.ui.components.TrustGuaranteeDialog
import com.example.ui.components.getTradeIcon
import com.example.ui.theme.GharGoBlue
import com.example.ui.theme.GharGoOrange

@Composable
fun LandingScreen(
    onFindService: () -> Unit,
    onJoinAsWorker: () -> Unit,
    onSelectTrade: (String) -> Unit,
    onOpenRateCard: () -> Unit,
    onNavigateToEmergency: () -> Unit = {},
    onSearchSubmit: (String) -> Unit = {},
    currentLocation: String = "Connaught Place, New Delhi",
    onLocationChange: (String) -> Unit = {},
    onOpenLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLocationDialog by remember { mutableStateOf(false) }
    var trustDialogType by remember { mutableStateOf<TrustDialogType?>(null) }
    var searchInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val quickSearches = remember {
        listOf(
            "⚡ Fan Repair",
            "🚰 Tap Leak",
            "❄️ AC Service",
            "🔑 Lockout",
            "🧹 Deep Clean"
        )
    }

    if (showLocationDialog) {
        LocationSelectorDialog(
            currentLocation = currentLocation,
            onLocationSelected = { loc -> onLocationChange(loc) },
            onDismiss = { showLocationDialog = false }
        )
    }

    trustDialogType?.let { type ->
        TrustGuaranteeDialog(
            type = type,
            onDismiss = { trustDialogType = null },
            onExploreServices = {
                trustDialogType = null
                onFindService()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Light Hero Container with Soft Pastel Gradient & Generous Spacing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEFF6FF), // Soft ice blue tint
                            Color(0xFFF8FAFC), // Off-white clean transition
                            Color(0xFFFFFFFF)  // Pure white bottom
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Location Bar & Status Pill Row with Quick Sign In
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location Pill
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(24.dp))
                            .clickable { showLocationDialog = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("landing_location_pill"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = GharGoBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "To: ",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = currentLocation,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "▾",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GharGoBlue
                        )
                    }

                    // Sign In / Account Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(24.dp))
                            .clickable { onOpenLogin() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("landing_sign_in_pill"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Sign In",
                            tint = GharGoBlue,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign In",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GharGoBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Brand Pill Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFDBEAFE))
                        .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ghargo_app_logo_1789204395305),
                        contentDescription = "GHARgo Logo",
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GHARgo • Verified Home Services",
                        color = Color(0xFF1D4ED8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Clear & Engaging Main Headline
                Text(
                    text = "What service does your home need today?",
                    color = Color(0xFF0F172A),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.3).sp,
                    modifier = Modifier.testTag("landing_tagline")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Connect directly with background-checked electricians, plumbers, and technicians with fixed rates and 0% middleman commission.",
                    color = Color(0xFF475569),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Active Interactive Search Bar Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchInput,
                                onValueChange = { searchInput = it },
                                placeholder = {
                                    Text(
                                        text = "Search service, e.g. Electrician, Tap Leak...",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = GharGoBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchInput.isNotBlank()) {
                                        IconButton(onClick = { searchInput = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                tint = Color(0xFF94A3B8),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        focusManager.clearFocus()
                                        if (searchInput.isNotBlank()) {
                                            onSearchSubmit(searchInput.trim())
                                        } else {
                                            onFindService()
                                        }
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GharGoBlue,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("landing_search_input")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    if (searchInput.isNotBlank()) {
                                        onSearchSubmit(searchInput.trim())
                                    } else {
                                        onFindService()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GharGoBlue),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("landing_find_service_button")
                            ) {
                                Text(
                                    text = "Find Pro",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Quick Search suggestion chips
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickSearches.forEach { item ->
                                val cleanQuery = item.substringAfter(" ").trim()
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                        .clickable {
                                            searchInput = cleanQuery
                                            onSearchSubmit(cleanQuery)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                        .testTag("quick_search_$cleanQuery")
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Trust Guarantee Chips (Interactive Light Pastel Style)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrustChip(
                        label = "✓ 100% Verified",
                        bg = Color(0xFFECFDF5),
                        text = Color(0xFF065F46),
                        border = Color(0xFFA7F3D0),
                        onClick = { trustDialogType = TrustDialogType.VERIFICATION },
                        tag = "chip_verified"
                    )
                    TrustChip(
                        label = "₹ Fixed Rates",
                        bg = Color(0xFFEFF6FF),
                        text = Color(0xFF1E40AF),
                        border = Color(0xFFBFDBFE),
                        onClick = onOpenRateCard,
                        tag = "chip_rates"
                    )
                    TrustChip(
                        label = "⚡ 15m Dispatch",
                        bg = Color(0xFFFFF7ED),
                        text = Color(0xFF9A3412),
                        border = Color(0xFFFED7AA),
                        onClick = onNavigateToEmergency,
                        tag = "chip_emergency"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Popular Home Service Categories (Light & Colorful Card Grid)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Popular Home Services",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Standardized fixed rate artisan guilds",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onFindService() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("landing_see_all_categories"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "See All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GharGoBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "See All",
                        tint = GharGoBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Grid of categories
            val categories = listOf(
                ServiceCategorySpec("Electrician", "₹249", Color(0xFFFEF3C7), Color(0xFFD97706)),
                ServiceCategorySpec("Plumber", "₹199", Color(0xFFE0F2FE), Color(0xFF0284C7)),
                ServiceCategorySpec("Carpenter", "₹299", Color(0xFFFFEDD5), Color(0xFFEA580C)),
                ServiceCategorySpec("Technician", "₹299", Color(0xFFCFFAFE), Color(0xFF0891B2)),
                ServiceCategorySpec("Painter", "₹350", Color(0xFFEDE9FE), Color(0xFF7C3AED)),
                ServiceCategorySpec("Cleaner", "₹299", Color(0xFFD1FAE5), Color(0xFF059669)),
                ServiceCategorySpec("Caregiver", "₹399", Color(0xFFFFE4E6), Color(0xFFE11D48)),
                ServiceCategorySpec("Gardener", "₹249", Color(0xFFECFCCB), Color(0xFF65A30D))
            )

            categories.chunked(4).forEach { rowList ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowList.forEach { spec ->
                        LightCategoryCard(
                            trade = spec.name,
                            startingPrice = spec.price,
                            iconBg = spec.bg,
                            iconTint = spec.tint,
                            onClick = { onSelectTrade(spec.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 24/7 Emergency Dispatch Banner (Light Coral Aesthetic) - Fully functional
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
            border = BorderStroke(1.dp, Color(0xFFFECDD3)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 20.dp)
                .clickable { onNavigateToEmergency() }
                .testTag("landing_emergency_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE4E6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Emergency",
                        tint = Color(0xFFE11D48),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "24/7 Priority Emergency",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color(0xFF9F1239)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE11D48))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "15m ARRIVAL",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Water leak, power outage, or broken lock? Standby artisans ready.",
                        fontSize = 11.sp,
                        color = Color(0xFF881337),
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onNavigateToEmergency,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("landing_emergency_dispatch_btn")
                ) {
                    Text(
                        text = "Dispatch",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // The GHARgo Advantage (Clean Light Feature Cards with Click Actions)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "The GHARgo Advantage",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            LightPillarCard(
                icon = Icons.Default.Verified,
                iconBg = Color(0xFFEFF6FF),
                iconTint = GharGoBlue,
                title = "100% Aadhaar & Police Verified",
                subtitle = "National Skill Registry Level 4 Certified",
                description = "Every artisan undergoes physical identity verification, police clearance, and hands-on peer guild assessment before serving your home.",
                onClick = { trustDialogType = TrustDialogType.VERIFICATION },
                tag = "pillar_verified"
            )

            LightPillarCard(
                icon = Icons.Default.ReceiptLong,
                iconBg = Color(0xFFECFDF5),
                iconTint = Color(0xFF059669),
                title = "Zero Surge • Fixed Rate Card",
                subtitle = "Upfront pricing without bargaining",
                description = "Standardized rates ratified across all service categories. No surprise charges, weekend surge fees, or post-job disputes.",
                onClick = onOpenRateCard,
                tag = "pillar_rates"
            )

            LightPillarCard(
                icon = Icons.Default.Security,
                iconBg = Color(0xFFFFF7ED),
                iconTint = GharGoOrange,
                title = "Two-Way OTP & Satisfaction Guarantee",
                subtitle = "Secure Jan Dhan UPI settlement",
                description = "Work begins only upon sharing your 4-digit start OTP. Payment is released safely once you verify completed workmanship.",
                onClick = { trustDialogType = TrustDialogType.OTP_SECURITY },
                tag = "pillar_otp"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Standard Rate Card Banner (Clean Light Style - Fully Clickable)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 20.dp)
                .clickable { onOpenRateCard() }
                .testTag("landing_rate_card_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = GharGoBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Standard Transparent Rates",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Browse benchmark rates across 30+ home services.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Button(
                    onClick = onOpenRateCard,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GharGoBlue),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("landing_view_rates_button")
                ) {
                    Text(
                        text = "View Rates",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Join as Artisan Banner (Soft Green Aesthetic - Fully Clickable)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 20.dp)
                .clickable { onJoinAsWorker() }
                .testTag("landing_join_worker_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Handshake,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Are You a Skilled Artisan?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14532D)
                    )
                    Text(
                        text = "Earn 100% of customer fee with cooperative ownership & health cover.",
                        fontSize = 11.sp,
                        color = Color(0xFF166534),
                        lineHeight = 15.sp
                    )
                }

                Button(
                    onClick = onJoinAsWorker,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("landing_join_worker_button")
                ) {
                    Text(
                        text = "Join Pro",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

private data class ServiceCategorySpec(
    val name: String,
    val price: String,
    val bg: Color,
    val tint: Color
)

@Composable
private fun LightCategoryCard(
    trade: String,
    startingPrice: String,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .clickable { onClick() }
            .testTag("trade_landing_$trade")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                .clip(CircleShape)
                .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTradeIcon(trade),
                    contentDescription = trade,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trade,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = startingPrice,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LightPillarCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    description: String,
    onClick: () -> Unit = {},
    tag: String = ""
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Details",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = iconTint
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun TrustChip(
    label: String,
    bg: Color,
    text: Color,
    border: Color,
    onClick: () -> Unit = {},
    tag: String = ""
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .testTag(tag)
    ) {
        Text(
            text = label,
            color = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
