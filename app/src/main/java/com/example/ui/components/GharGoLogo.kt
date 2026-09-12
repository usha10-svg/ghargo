package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GharGoBlue
import com.example.ui.theme.GharGoOrange

@Composable
fun GharGoLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 36.dp,
    titleSize: TextUnit = 20.sp,
    showTagline: Boolean = true,
    taglineText: String = "On-Demand Home Services",
    lightText: Boolean = true
) {
    Row(
        modifier = modifier.testTag("ghargo_brand_logo"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo Icon
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ghargo_app_logo_1789204395305),
                contentDescription = "GHARgo Logo",
                modifier = Modifier.size(iconSize),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "GHAR",
                    color = if (lightText) Color.White else Color(0xFF0F172A),
                    fontWeight = FontWeight.Black,
                    fontSize = titleSize,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(GharGoOrange)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "go",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (titleSize.value * 0.75f).sp
                    )
                }
            }

            if (showTagline) {
                Text(
                    text = taglineText,
                    color = if (lightText) Color.White.copy(alpha = 0.75f) else Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
