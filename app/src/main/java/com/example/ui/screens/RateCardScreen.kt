package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CooperativeNavy
import com.example.ui.theme.GharGoBlue
import com.example.ui.theme.SaffronTrust
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WelfareGreen
import com.example.ui.theme.WelfareGreenLight
import com.example.viewmodel.RateCardItem
import com.example.viewmodel.ShramViewModel

/**
 * Dedicated Transparent Rate Card Page.
 * Displays official cooperative-governed standard labor pricing.
 */
@Composable
fun RateCardScreen(
    onSelectTrade: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTradeFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val allRates = remember { ShramViewModel.standardRateCards }
    val trades = remember {
        listOf("ALL") + allRates.map { it.trade }.distinct()
    }

    val filteredRates = remember(selectedTradeFilter, searchQuery) {
        allRates.filter { item ->
            val matchesTrade = if (selectedTradeFilter == "ALL") true else item.trade.equals(selectedTradeFilter, ignoreCase = true)
            val matchesQuery = if (searchQuery.isBlank()) true else {
                item.serviceName.contains(searchQuery, ignoreCase = true) ||
                item.trade.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
            }
            matchesTrade && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back navigation bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("rate_card_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CooperativeNavy
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Standard Rate Card",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CooperativeNavy
                )
            }
        }
        // Banner Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(WelfareGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = WelfareGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Standard Fair Price Card",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CooperativeNavy
                                )
                                Text(
                                    text = "Fixed transparent rates across Delhi NCR",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WelfareGreenLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ZERO SURGE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = WelfareGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Every rupee paid goes directly to the certified artisan's bank account with 0% corporate broker deduction and a mandatory 3% social welfare fund reserve.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search services e.g. Switchboard, AC, Tank clean...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronTrust,
                    unfocusedBorderColor = SurfaceBorder
                )
            )
        }

        // Trade category chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trades.forEach { trade ->
                    val isSelected = selectedTradeFilter == trade
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTradeFilter = trade },
                        label = { Text(if (trade == "ALL") "All Trades" else trade, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CooperativeNavy,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        // Rate Card Items
        items(filteredRates) { item ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .testTag("rate_item_${item.serviceName.lowercase().replace(" ", "_")}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.trade.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.serviceName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CooperativeNavy
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.description,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${item.cooperativeStandardRate}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SaffronTrust
                            )
                            Text(
                                text = item.unit,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Corporate: ₹${item.typicalMarketRate}",
                                fontSize = 10.sp,
                                color = TextMuted,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = SurfaceBorder)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WelfareGreen, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save ₹${item.typicalMarketRate - item.cooperativeStandardRate}", fontSize = 11.sp, color = WelfareGreen, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onSelectTrade(item.trade) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GharGoBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("book_rate_${item.trade.lowercase()}")
                        ) {
                            Text("Find ${item.trade}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
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
