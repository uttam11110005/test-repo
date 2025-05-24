package com.example.ordertracker.data.model

val Order.totalRevenue: Double
    get() = quantity * sellingPrice

val Order.totalProductionCost: Double
    get() = quantity * productionCost

val Order.profit: Double
    get() = totalRevenue - totalProductionCost

val Order.profitMarginPercentage: Double
    get() = if (totalRevenue > 0) (profit / totalRevenue) * 100 else 0.0

val Order.costBreakdownPercentage: Double // Renamed from costBreakdownPercentage for clarity (it's cost as % of revenue)
    get() = if (totalRevenue > 0) (totalProductionCost / totalRevenue) * 100 else 0.0
