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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight

/**
 * Modern, clean Profile & Settings Screen.
 * Displays user identity, mode switching (Customer vs Worker),
 * cooperative membership details, and Logout / Login.
 */
@Composable
fun ProfileScreen(
    userName: String,
    userPhone: String,
    userRole: String,
    isLoggedIn: Boolean,
    onRoleToggle: (String) -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenLogin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (userRole == "WORKER") SaffronTrust else CooperativeNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (userRole == "WORKER") Icons.Default.Engineering else Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isLoggedIn) userName else "Guest User",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CooperativeNavy
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (isLoggedIn) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = WelfareGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isLoggedIn) userPhone else "Sign in to save bookings & track history",
                                fontSize = 12.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (userRole == "WORKER") Color(0xFFFEF3C7) else Color(0xFFEFF6FF))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (userRole == "WORKER") "Artisan Guild Member #SHR-1049" else "Verified Consumer Member",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userRole == "WORKER") Color(0xFFB45309) else Color(0xFF1D4ED8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = SurfaceBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Role Switch Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Active Mode: ${if (userRole == "WORKER") "Artisan / Worker Portal" else "Customer / Hire Mode"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CooperativeNavy
                            )
                            Text(
                                text = if (userRole == "WORKER") "Manage jobs, wallet & duty calendar" else "Find & book certified artisans",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Switch(
                            checked = userRole == "WORKER",
                            onCheckedChange = { isWorker ->
                                onRoleToggle(if (isWorker) "WORKER" else "CUSTOMER")
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SaffronTrust
                            )
                        )
                    }
                }
            }
        }

        // Quick Worker Calendar Shortcut
        if (userRole == "WORKER") {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .clickable { onOpenCalendar() }
                        .testTag("profile_open_calendar")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(WelfareGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Artisan Availability Calendar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WelfareGreen
                            )
                            Text(
                                text = "Mark yourself Available, Busy, Leave or Emergency",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = WelfareGreen)
                    }
                }
            }
        }

        // App Options
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuItem(
                        icon = Icons.Default.Shield,
                        title = "e-Shram & Social Welfare Shield",
                        subtitle = "Accidental insurance & pension link verified"
                    )
                    HorizontalDivider(color = SurfaceBorder, modifier = Modifier.padding(horizontal = 14.dp))

                    ProfileMenuItem(
                        icon = Icons.Default.Language,
                        title = "Language Preference",
                        subtitle = "English / हिन्दी (Hindi)"
                    )
                    HorizontalDivider(color = SurfaceBorder, modifier = Modifier.padding(horizontal = 14.dp))

                    ProfileMenuItem(
                        icon = Icons.Default.Handshake,
                        title = "Cooperative Bylaws & Fair Pay Guarantee",
                        subtitle = "0% Commercial commission charter"
                    )
                }
            }
        }

        // Login / Logout Action Button
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (isLoggedIn) {
                        Button(
                            onClick = onLogout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("logout_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Log Out / Switch Account",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    } else {
                        Button(
                            onClick = onOpenLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("open_login_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CooperativeNavy)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign In / Register with Mobile OTP",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
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

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CooperativeNavy,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = CooperativeNavy
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
