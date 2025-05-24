package com.example.ordertracker.ui.addorder

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ordertracker.data.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(
    addOrderViewModel: AddOrderViewModel = viewModel(
        factory = AddOrderViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val context = LocalContext.current

    var orderName by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var productionCost by remember { mutableStateOf("") }
    var orderDateString by remember { mutableStateOf("") } // YYYY-MM-DD

    val deliveryStatusOptions = listOf("Pending", "Delivered")
    var selectedDeliveryStatus by remember { mutableStateOf(deliveryStatusOptions[0]) }
    var expanded by remember { mutableStateOf(false) }

    // SimpleDateFormat for date parsing
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Add New Order", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = orderName,
            onValueChange = { orderName = it },
            label = { Text("Order Name*") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sellingPrice,
            onValueChange = { sellingPrice = it },
            label = { Text("Selling Price*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = productionCost,
            onValueChange = { productionCost = it },
            label = { Text("Production Cost*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = orderDateString,
            onValueChange = { orderDateString = it },
            label = { Text("Order Date (YYYY-MM-DD)*") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g., 2023-12-25") }
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDeliveryStatus,
                    onValueChange = {},
                    label = { Text("Delivery Status*") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    deliveryStatusOptions.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                selectedDeliveryStatus = status
                                expanded = false
                            }
                        )
                    }
                }
            }
        }


        Button(
            onClick = {
                // Validation
                if (orderName.isBlank() || quantity.isBlank() || sellingPrice.isBlank() || productionCost.isBlank() || orderDateString.isBlank()) {
                    Toast.makeText(context, "Please fill all required fields (*)", Toast.LENGTH_LONG).show()
                    return@Button
                }
                val quant = quantity.toIntOrNull()
                if (quant == null || quant <= 0) {
                    Toast.makeText(context, "Quantity must be a positive number", Toast.LENGTH_LONG).show()
                    return@Button
                }
                val sellPrice = sellingPrice.toDoubleOrNull()
                if (sellPrice == null || sellPrice < 0) {
                    Toast.makeText(context, "Selling price must be a valid number", Toast.LENGTH_LONG).show()
                    return@Button
                }
                val prodCost = productionCost.toDoubleOrNull()
                if (prodCost == null || prodCost < 0) {
                    Toast.makeText(context, "Production cost must be a valid number", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val orderDateLong: Long = try {
                    sdf.parse(orderDateString)?.time ?: System.currentTimeMillis()
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid date format. Use YYYY-MM-DD", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val newOrder = Order(
                    orderName = orderName,
                    customerName = customerName.ifBlank { null },
                    date = orderDateLong,
                    quantity = quant,
                    sellingPrice = sellPrice,
                    productionCost = prodCost,
                    deliveryStatus = selectedDeliveryStatus
                )
                addOrderViewModel.addOrder(newOrder)
                Toast.makeText(context, "Order Added Successfully!", Toast.LENGTH_SHORT).show()

                // Clear fields
                orderName = ""
                customerName = ""
                quantity = ""
                sellingPrice = ""
                productionCost = ""
                orderDateString = ""
                selectedDeliveryStatus = deliveryStatusOptions[0]
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Save Order")
        }
    }
}
