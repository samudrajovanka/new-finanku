package com.mobprog.finanku.data

data class Expenses(
    val date: String,
    val amount: Int,
    val description: String,
    val type: DataCategory,
)

data class DataCategory(
    val data: CategoryResponse
)