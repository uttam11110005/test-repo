package com.example.ordertracker.ui.orderslist

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ordertracker.data.model.Order
import com.example.ordertracker.data.model.costBreakdownPercentage // Extension
import com.example.ordertracker.data.model.profit // Extension
import com.example.ordertracker.data.model.profitMarginPercentage // Extension
import com.example.ordertracker.data.model.totalProductionCost // Extension
import com.example.ordertracker.data.model.totalRevenue // Extension
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(
    viewModel: OrdersListViewModel = viewModel(
        factory = OrdersListViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val orders by viewModel.orders.collectAsState()
    val currentFilter by viewModel.filterType.collectAsState()

    val sdf = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter Radio Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrderFilterType.values().forEach { filterType ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (filterType == currentFilter),
                        onClick = { viewModel.setFilterType(filterType) }
                    )
                    Text(
                        text = filterType.name.lowercase(Locale.getDefault())
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No orders found for the selected filter.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderItemCard(
                        order = order,
                        sdf = sdf,
                        onMarkAsComplete = {
                            val updatedOrder = order.copy(deliveryStatus = "Delivered")
                            viewModel.updateOrder(updatedOrder)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order, sdf: SimpleDateFormat, onMarkAsComplete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Order: ${order.orderName}", style = MaterialTheme.typography.titleLarge)
            order.customerName?.let {
                Text(text = "Customer: $it", style = MaterialTheme.typography.titleMedium)
            }
            Text(text = "Date: ${sdf.format(Date(order.date))}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Quantity: ${order.quantity}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: ${order.deliveryStatus}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(6.dp))
            Divider()
            Spacer(modifier = Modifier.height(6.dp))

            Text("Financials:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
            val percentageFormat = remember { NumberFormat.getPercentInstance(Locale.getDefault()).apply{ minimumFractionDigits = 1 } }

            FinancialDetailRow("Unit Price:", currencyFormat.format(order.sellingPrice))
            FinancialDetailRow("Unit Cost:", currencyFormat.format(order.productionCost))

            Spacer(modifier = Modifier.height(4.dp))

            FinancialDetailRow("Total Revenue:", currencyFormat.format(order.totalRevenue))
            FinancialDetailRow("Total Prod. Cost:", "${currencyFormat.format(order.totalProductionCost)} (${percentageFormat.format(order.costBreakdownPercentage / 100)})")
            FinancialDetailRow("Total Profit:", "${currencyFormat.format(order.profit)} (${percentageFormat.format(order.profitMarginPercentage / 100)})", isProfit = true, profitValue = order.profit)


            if (order.deliveryStatus != "Delivered") {
                Button(
                    onClick = onMarkAsComplete,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.End)
                ) {
                    Text("Mark as Delivered")
                }
            }
        }
    }
}

@Composable
fun FinancialDetailRow(label: String, value: String, isProfit: Boolean = false, profitValue: Double = 0.0) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isProfit) {
                if (profitValue < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            } else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
