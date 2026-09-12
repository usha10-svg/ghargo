package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.WorkerEntity
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.VerifiedBadgeBlue

/**
 * Returns the appropriate photo drawable resource for a worker based on ID and gender.
 */
fun getWorkerPhotoRes(worker: WorkerEntity): Int {
    return if (worker.gender.equals("Female", ignoreCase = true)) {
        R.drawable.img_worker_female
    } else {
        R.drawable.img_worker_male
    }
}

/**
 * Reusable worker photo avatar with high-resolution photo, cooperative border,
 * trade symbol mini-badge, and verification badge overlay.
 */
@Composable
fun WorkerPhotoAvatar(
    worker: WorkerEntity,
    size: Dp = 64.dp,
    showVerificationBadge: Boolean = true,
    modifier: Modifier = Modifier
) {
    val photoRes = getWorkerPhotoRes(worker)
    val badgeSize = (size.value * 0.32f).coerceAtLeast(16f).dp

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Main Photo with rounded circular crop and cooperative border
        Image(
            painter = painterResource(id = photoRes),
            contentDescription = "Photo of ${worker.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, SaffronTrust, CircleShape)
        )

        // Trade Icon Mini-Badge (Top Right / Bottom Left)
        Box(
            modifier = Modifier
                .size(badgeSize)
                .align(Alignment.BottomStart)
                .offset(x = (-2).dp, y = 2.dp)
                .clip(CircleShape)
                .background(CooperativeNavy)
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getTradeIcon(worker.trade),
                contentDescription = worker.trade,
                tint = SaffronTrust,
                modifier = Modifier.size(badgeSize * 0.65f)
            )
        }

        // Verification Badge (Bottom Right)
        if (showVerificationBadge && (worker.verifiedEShram || worker.verifiedNsdc)) {
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified Artisan",
                    tint = VerifiedBadgeBlue,
                    modifier = Modifier.size(badgeSize)
                )
            }
        }
    }
}
