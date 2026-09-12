package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Yard
import androidx.compose.ui.graphics.vector.ImageVector

fun getTradeIcon(trade: String): ImageVector {
    return when (trade.lowercase().trim()) {
        "electrician" -> Icons.Default.ElectricBolt
        "plumber" -> Icons.Default.Plumbing
        "carpenter" -> Icons.Default.Carpenter
        "painter" -> Icons.Default.FormatPaint
        "cleaner" -> Icons.Default.CleaningServices
        "caregiver" -> Icons.Default.VolunteerActivism
        "driver" -> Icons.Default.DirectionsCar
        "gardener" -> Icons.Default.Yard
        "technician" -> Icons.Default.HomeRepairService
        "appliance tech" -> Icons.Default.HomeRepairService
        "mason" -> Icons.Default.Engineering
        "ac tech" -> Icons.Default.Speed
        "solar tech" -> Icons.Default.SolarPower
        else -> Icons.Default.Handyman
    }
}
