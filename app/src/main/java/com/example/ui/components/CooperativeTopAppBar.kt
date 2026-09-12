package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.CooperativeNavyDark
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.WelfareGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CooperativeTopAppBar(
    currentRole: String,
    onRoleToggle: (String) -> Unit,
    onOpenRateCard: () -> Unit,
    isLanding: Boolean = false,
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        color = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.border(width = (0.5).dp, color = Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // GHARgo Brand Logo in clean dark/electric styling
            GharGoLogo(
                modifier = Modifier.clickable { onHomeClick() },
                iconSize = 34.dp,
                titleSize = 18.sp,
                showTagline = true,
                taglineText = "On-Demand Home Services",
                lightText = false
            )

            // Right Actions: Mode Pill Toggle + Profile Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isCustomer = currentRole == "CUSTOMER"

                // Compact Role Switch Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isCustomer) Color(0xFFEFF6FF) else Color(0xFFFFF7ED))
                        .border(
                            1.dp,
                            if (isCustomer) Color(0xFFBFDBFE) else Color(0xFFFED7AA),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            onRoleToggle(if (isCustomer) "WORKER" else "CUSTOMER")
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("role_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCustomer) Icons.Default.Person else Icons.Default.Engineering,
                            contentDescription = null,
                            tint = if (isCustomer) Color(0xFF2563EB) else Color(0xFFEA580C),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isCustomer) "Customer" else "Artisan",
                            color = if (isCustomer) Color(0xFF1E40AF) else Color(0xFF9A3412),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "⇄",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }

                // Profile / Login Icon Button
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("app_bar_profile_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile & Account",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
