package com.example.ordertracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ordertracker.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM orders ORDER BY date DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: Int): Flow<Order>

    @Query("SELECT * FROM orders WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE deliveryStatus = :status ORDER BY date DESC")
    fun getOrdersByStatus(status: String): Flow<List<Order>>

    // Method name changed to getOrdersForDate as per requirement, though signature is more like getOrdersForDateRange
    // The implementation will use this to get orders for a single logical day.
    @Query("SELECT * FROM orders WHERE date >= :startOfDayTimestamp AND date < :endOfDayTimestamp ORDER BY date DESC")
    fun getOrdersForDate(startOfDayTimestamp: Long, endOfDayTimestamp: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE date >= :startOfMonthTimestamp AND date < :endOfMonthTimestamp ORDER BY date DESC")
    fun getOrdersForMonth(startOfMonthTimestamp: Long, endOfMonthTimestamp: Long): Flow<List<Order>>
}
