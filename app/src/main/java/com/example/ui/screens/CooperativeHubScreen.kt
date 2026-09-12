package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight

@Composable
fun CooperativeHubScreen(
    onOpenRateCard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Mission Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CooperativeNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SaffronTrust),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "The Cooperative Mission",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Smart India Hackathon (SIH) 2024",
                                color = SaffronTrust,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "GHARgo replaces high-fee aggregator platforms with a direct artisan marketplace. By eliminating the 25% middleman commission, workers earn honest livelihoods, customers pay clear standardized rates, and every service is backed by verified background checks.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Live Impact Metrics Grid
            Text(
                text = "Cooperative Chapter Live Impact (Delhi NCR)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CooperativeNavy
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "100% Direct",
                    value = "₹48.2 Lakhs",
                    subtitle = "Retained in Worker Families",
                    iconColor = WelfareGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "0% Commission",
                    value = "₹12.1 Lakhs",
                    subtitle = "Saved by Customers",
                    iconColor = SaffronTrust,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Welfare Claims",
                    value = "142 Families",
                    subtitle = "Ayushman PM-JAY Covered",
                    iconColor = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Verified Guild",
                    value = "1,248 Artisans",
                    subtitle = "e-Shram & NSDC Certified",
                    iconColor = CooperativeNavy,
                    modifier = Modifier.weight(1f)
                )
            }

            // Pillars of GHARgo
            Text(
                text = "How GHARgo Protects Everyone",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CooperativeNavy
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PillarItem(
                        icon = Icons.Default.AttachMoney,
                        title = "Fair Transparent Rate Card",
                        description = "Democratic fixed rates. No surge gouging in peak heatwaves or monsoon seasons.",
                        color = WelfareGreen
                    )
                    Divider(color = SurfaceBorder)
                    PillarItem(
                        icon = Icons.Default.Security,
                        title = "Safety & Two-Way OTP Verification",
                        description = "Both customer and artisan confirm identity using secure 4-digit token authentication upon arrival.",
                        color = SaffronTrust
                    )
                    Divider(color = SurfaceBorder)
                    PillarItem(
                        icon = Icons.Default.HealthAndSafety,
                        title = "3% Worker Welfare & Social Security",
                        description = "Every booking allocates 3% into a shared fund providing hospitalization and accidental tool loss coverage.",
                        color = Color(0xFF2563EB)
                    )
                    Divider(color = SurfaceBorder)
                    PillarItem(
                        icon = Icons.Default.HowToVote,
                        title = "Democratic Worker Assembly",
                        description = "Workers hold equity shares and directly vote on minimum wage rules and cooperative policy proposals.",
                        color = CooperativeNavy
                    )
                }
            }

            // Action CTA: View Rate Card
            Button(
                onClick = onOpenRateCard,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronTrust),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("hub_open_rate_card_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View Cooperative Standard Rate Card",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = iconColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
fun PillarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
        }
    }
}
