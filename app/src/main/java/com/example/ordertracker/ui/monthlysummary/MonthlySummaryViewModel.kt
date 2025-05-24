package com.example.ordertracker.ui.monthlysummary

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ordertracker.data.AppDatabase
import com.example.ordertracker.data.dao.OrderDao
import com.example.ordertracker.data.model.Order
import com.example.ordertracker.ui.dailysummary.DailySummaryData // Reusing DailySummaryData
import com.example.ordertracker.utils.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class MonthlySummaryViewModel(private val orderDao: OrderDao) : ViewModel() {

    private val _currentCalendar = Calendar.getInstance()

    private val _selectedYear = MutableStateFlow(_currentCalendar.get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(_currentCalendar.get(Calendar.MONTH) + 1) // Calendar.MONTH is 0-indexed
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    val monthlySummary: StateFlow<DailySummaryData> =
        combine(_selectedYear, _selectedMonth) { year, month ->
            Pair(year, month)
        }.flatMapLatest { (year, month) ->
            val startOfMonth = DateUtils.getStartOfMonthTimestamp(year, month)
            val endOfMonth = DateUtils.getEndOfMonthTimestamp(year, month)
            orderDao.getOrdersForMonth(startOfMonth, endOfMonth)
                .map { orders ->
                    calculateSummary(orders)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailySummaryData()
        )

    fun selectMonthYear(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
    }

    fun changeMonth(offset: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, _selectedYear.value)
            set(Calendar.MONTH, _selectedMonth.value - 1) // To 0-indexed
            add(Calendar.MONTH, offset)
        }
        _selectedYear.value = calendar.get(Calendar.YEAR)
        _selectedMonth.value = calendar.get(Calendar.MONTH) + 1 // Back to 1-indexed
    }

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

class MonthlySummaryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MonthlySummaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MonthlySummaryViewModel(AppDatabase.getInstance(application).orderDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
