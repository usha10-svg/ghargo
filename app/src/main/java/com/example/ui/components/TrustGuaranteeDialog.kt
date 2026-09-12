package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.theme.GharGoBlue

enum class TrustDialogType {
    VERIFICATION,
    OTP_SECURITY
}

@Composable
fun TrustGuaranteeDialog(
    type: TrustDialogType,
    onDismiss: () -> Unit,
    onExploreServices: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (type == TrustDialogType.VERIFICATION) Color(0xFFEFF6FF) else Color(0xFFFFF7ED)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (type == TrustDialogType.VERIFICATION) Icons.Default.Verified else Icons.Default.Security,
                            contentDescription = null,
                            tint = if (type == TrustDialogType.VERIFICATION) GharGoBlue else Color(0xFFEA580C),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (type == TrustDialogType.VERIFICATION) "100% Artisan Verification" else "Two-Way OTP & Safety",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = if (type == TrustDialogType.VERIFICATION) "National Guild Quality Guarantee" else "Zero-Surge & Safe Escrow",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (type == TrustDialogType.VERIFICATION) {
                    TrustCheckItem(
                        icon = Icons.Default.CheckCircle,
                        iconTint = Color(0xFF059669),
                        title = "1. Aadhaar & e-Shram National Registry",
                        desc = "Direct UIDAI biometrics validation ensuring verified citizen identities."
                    )
                    TrustCheckItem(
                        icon = Icons.Default.Shield,
                        iconTint = GharGoBlue,
                        title = "2. Police Background Clearance",
                        desc = "Zero criminal record certification submitted and periodically renewed."
                    )
                    TrustCheckItem(
                        icon = Icons.Default.WorkspacePremium,
                        iconTint = Color(0xFFD97706),
                        title = "3. NSDC Skill Level 4 Assessment",
                        desc = "Practical trades assessment certified by National Skill Development Corporation."
                    )
                    TrustCheckItem(
                        icon = Icons.Default.Verified,
                        iconTint = Color(0xFF7C3AED),
                        title = "4. Peer Guild Master Review",
                        desc = "Trade master committee review with ongoing 4.8+ minimum customer rating mandate."
                    )
                } else {
                    TrustCheckItem(
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFF059669),
                        title = "Step 1: Secure 4-Digit Start OTP",
                        desc = "Artisan only begins work after verifying the secret token generated in your app."
                    )
                    TrustCheckItem(
                        icon = Icons.Default.Shield,
                        iconTint = GharGoBlue,
                        title = "Step 2: Transparent Fixed Rate Guarantee",
                        desc = "Zero bargaining, zero sudden markups. The price quoted is the price settled."
                    )
                    TrustCheckItem(
                        icon = Icons.Default.CheckCircle,
                        iconTint = Color(0xFFEA580C),
                        title = "Step 3: Post-Work Inspection",
                        desc = "Inspect the repair and verify thorough clean-up before signing off."
                    )
                    TrustCheckItem(
                        icon = Icons.Default.WorkspacePremium,
                        iconTint = Color(0xFF7C3AED),
                        title = "Step 4: Safe Jan Dhan UPI Settlement",
                        desc = "100% of your payment directly reaches the artisan with zero middleman commissions."
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onExploreServices()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GharGoBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dialog_explore_verified_btn")
            ) {
                Text("Find Verified Artisans", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun TrustCheckItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    desc: String
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
