package com.example.ordertracker.ui.orderslist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ordertracker.data.AppDatabase
import com.example.ordertracker.data.dao.OrderDao
import com.example.ordertracker.data.model.Order
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class OrderFilterType {
    ALL, PENDING, DELIVERED
}

class OrdersListViewModel(private val orderDao: OrderDao) : ViewModel() {

    private val _filterType = MutableStateFlow(OrderFilterType.ALL)
    val filterType: StateFlow<OrderFilterType> = _filterType.asStateFlow()

    val orders: StateFlow<List<Order>> = _filterType
        .flatMapLatest { filter ->
            when (filter) {
                OrderFilterType.ALL -> orderDao.getAllOrders()
                OrderFilterType.PENDING -> orderDao.getOrdersByStatus("Pending")
                OrderFilterType.DELIVERED -> orderDao.getOrdersByStatus("Delivered")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilterType(filterType: OrderFilterType) {
        _filterType.value = filterType
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            orderDao.updateOrder(order)
        }
    }
}

class OrdersListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdersListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrdersListViewModel(AppDatabase.getInstance(application).orderDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
