package com.example.ordertracker.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ordertracker.data.AppDatabase
import com.example.ordertracker.data.dao.OrderDao
import com.example.ordertracker.data.model.Order
import com.example.ordertracker.ui.dailysummary.DailySummaryData // Reusing DailySummaryData
import com.example.ordertracker.utils.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Date

class HomeViewModel(private val orderDao: OrderDao) : ViewModel() {

    private val todayStartTimestamp = DateUtils.getStartOfDayTimestamp(Date())
    private val todayEndTimestamp = DateUtils.getEndOfDayTimestamp(Date())

    val todaysOrders: StateFlow<List<Order>> =
        orderDao.getOrdersForDate(todayStartTimestamp, todayEndTimestamp)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val todaysSummary: StateFlow<DailySummaryData> =
        todaysOrders.map { orders ->
            calculateSummary(orders)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailySummaryData()
        )

    private fun calculateSummary(orders: List<Order>): DailySummaryData {
        if (orders.isEmpty()) {
            return DailySummaryData()
        }
        val totalOrders = orders.size
        val totalRevenue = orders.sumOf { it.quantity * it.sellingPrice }
        val totalCost = orders.sumOf { it.quantity * it.productionCost }
        val totalProfit = totalRevenue - totalCost
        return DailySummaryData(totalOrders, totalRevenue, totalCost, totalProfit)
    }
}

class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(AppDatabase.getInstance(application).orderDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
