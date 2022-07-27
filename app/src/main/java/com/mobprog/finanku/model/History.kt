package com.mobprog.finanku.model

data class History(
    val date: String,
    val description: String,
    val amount: Int,
    val type: CategoryExpenses,
)
