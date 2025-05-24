package com.example.ordertracker.ui.dailysummary

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DailySummaryScreen(
    viewModel: DailySummaryViewModel = viewModel(
        factory = DailySummaryViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val summaryData by viewModel.dailySummary.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (summaryData.totalOrders == 0) {
            Text("No orders for today.", style = MaterialTheme.typography.headlineSmall)
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Today's Summary",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
                    )

                    SummaryRow("Total Orders:", summaryData.totalOrders.toString())
                    SummaryRow("Total Revenue:", currencyFormat.format(summaryData.totalRevenue))
                    SummaryRow("Total Cost:", currencyFormat.format(summaryData.totalCost))
                    SummaryRow("Total Profit:", currencyFormat.format(summaryData.totalProfit), isProfit = true)
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isProfit: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = if (isProfit) FontWeight.Bold else FontWeight.Normal,
            color = if (isProfit) {
                if (value.startsWith("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            } else MaterialTheme.colorScheme.onSurface
        )
    }
}
