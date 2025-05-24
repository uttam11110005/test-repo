package com.example.ordertracker.ui.home

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ordertracker.data.model.Order
import com.example.ordertracker.ui.dailysummary.DailySummaryData
import com.example.ordertracker.ui.dailysummary.SummaryRow // Reusing SummaryRow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val todaysSummary by viewModel.todaysSummary.collectAsState()
    val todaysOrders by viewModel.todaysOrders.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Today's Overview",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
        )

        TodaySummarySection(summaryData = todaysSummary, currencyFormat = currencyFormat)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Today's Orders",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (todaysOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No orders scheduled for today.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todaysOrders, key = { it.id }) { order ->
                    TodaysOrderItem(order = order, dateFormat = dateFormat)
                }
            }
        }
    }
}

@Composable
fun TodaySummarySection(summaryData: DailySummaryData, currencyFormat: NumberFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryRow("Total Orders:", summaryData.totalOrders.toString())
            SummaryRow("Total Revenue:", currencyFormat.format(summaryData.totalRevenue))
            SummaryRow("Total Cost:", currencyFormat.format(summaryData.totalCost))
            SummaryRow("Total Profit:", currencyFormat.format(summaryData.totalProfit), isProfit = true)
        }
    }
}

@Composable
fun TodaysOrderItem(order: Order, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(order.orderName, style = MaterialTheme.typography.titleMedium)
                order.customerName?.let {
                    Text("Customer: $it", style = MaterialTheme.typography.bodySmall)
                }
                Text("Scheduled: ${dateFormat.format(Date(order.date))}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                order.deliveryStatus,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (order.deliveryStatus == "Delivered") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
