package com.example.ordertracker.ui.monthlysummary

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ordertracker.ui.dailysummary.DailySummaryData
import com.example.ordertracker.ui.dailysummary.SummaryRow // Reusing SummaryRow
import java.text.DateFormatSymbols
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MonthlySummaryScreen(
    viewModel: MonthlySummaryViewModel = viewModel(
        factory = MonthlySummaryViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val summaryData by viewModel.monthlySummary.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState() // 1-12

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    val monthNames = remember { DateFormatSymbols(Locale.getDefault()).months }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Monthly Summary",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        MonthYearSelector(
            selectedYear = selectedYear,
            selectedMonth = selectedMonth, // Pass 1-12
            monthNames = monthNames,
            onPreviousMonth = { viewModel.changeMonth(-1) },
            onNextMonth = { viewModel.changeMonth(1) }
            // More sophisticated year/month selection could be added here if needed
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (summaryData.totalOrders == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Takes up remaining space
                contentAlignment = Alignment.Center
            ) {
                Text("No orders for ${monthNames[selectedMonth - 1]} $selectedYear.", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            MonthlySummaryCard(summaryData = summaryData, currencyFormat = currencyFormat)
        }
    }
}

@Composable
fun MonthYearSelector(
    selectedYear: Int,
    selectedMonth: Int, // 1-12
    monthNames: Array<String>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
        }
        Text(
            text = "${monthNames[selectedMonth - 1]} $selectedYear", // selectedMonth is 1-12, array is 0-indexed
            style = MaterialTheme.typography.headlineSmall
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
        }
    }
}

@Composable
fun MonthlySummaryCard(summaryData: DailySummaryData, currencyFormat: NumberFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryRow("Total Orders:", summaryData.totalOrders.toString())
            SummaryRow("Total Revenue:", currencyFormat.format(summaryData.totalRevenue))
            SummaryRow("Total Cost:", currencyFormat.format(summaryData.totalCost))
            SummaryRow("Total Profit:", currencyFormat.format(summaryData.totalProfit), isProfit = true)
        }
    }
}
