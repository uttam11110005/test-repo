package com.example.ordertracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderName: String,
    val customerName: String?,
    val date: Long,
    val quantity: Int,
    val sellingPrice: Double,
    val productionCost: Double,
    val deliveryStatus: String
)
