package com.example.ordertracker.ui.addorder

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ordertracker.data.AppDatabase
import com.example.ordertracker.data.dao.OrderDao
import com.example.ordertracker.data.model.Order
import kotlinx.coroutines.launch

class AddOrderViewModel(private val orderDao: OrderDao) : ViewModel() {

    fun addOrder(order: Order) {
        viewModelScope.launch {
            orderDao.insertOrder(order)
        }
    }
}

class AddOrderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddOrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddOrderViewModel(AppDatabase.getInstance(application).orderDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
