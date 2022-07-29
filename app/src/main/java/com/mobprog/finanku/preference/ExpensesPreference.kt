package com.mobprog.finanku.preference

import android.content.Context

internal class ExpensesPreference(context: Context) {
    companion object {
        private const val PREFS_NAME = "expenses_pref"
        private const val FOOD = "food"
        private const val SHOP = "shop"
        private const val TRAVEL = "travel"
        private const val OTHERS = "others"
        private const val TOTAL = "total"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setExpenses(food: Int, shop: Int, travel: Int, others: Int, total: Int) {
        val editor = preferences.edit()
        editor.putInt(FOOD, food)
        editor.putInt(SHOP, shop)
        editor.putInt(TRAVEL, travel)
        editor.putInt(OTHERS, others)
        editor.putInt(TOTAL, total)

        editor.apply()
    }

    fun addFood(amount: Int) {
        val editor = preferences.edit()
        val food = preferences.getInt(FOOD, 0)
        val total = preferences.getInt(TOTAL, 0)

        editor.putInt(FOOD, food + amount)
        editor.putInt(TOTAL, total + amount)
        editor.apply()
    }

    fun addShop(amount: Int) {
        val editor = preferences.edit()
        val shop = preferences.getInt(SHOP, 0)
        val total = preferences.getInt(TOTAL, 0)

        editor.putInt(SHOP, shop + amount)
        editor.putInt(TOTAL, total + amount)
        editor.apply()
    }

    fun addTravel(amount: Int) {
        val editor = preferences.edit()
        val travel = preferences.getInt(TRAVEL, 0)
        val total = preferences.getInt(TOTAL, 0)

        editor.putInt(TRAVEL, travel + amount)
        editor.putInt(TOTAL, total + amount)
        editor.apply()
    }

    fun addOthers(amount: Int) {
        val editor = preferences.edit()
        val others = preferences.getInt(OTHERS, 0)
        val total = preferences.getInt(TOTAL, 0)

        editor.putInt(OTHERS, others + amount)
        editor.putInt(TOTAL, total + amount)
        editor.apply()
    }

    fun getFood(): Int {
        return preferences.getInt(FOOD, 0)
    }

    fun getShop(): Int {
        return preferences.getInt(SHOP, 0)
    }

    fun getTravel(): Int {
        return preferences.getInt(TRAVEL, 0)
    }

    fun getOthers(): Int {
        return preferences.getInt(OTHERS, 0)
    }

    fun getAllTotal(): Int {
        return preferences.getInt(TOTAL, 0)
    }

    fun reset() {
        val editor = preferences.edit()
        editor.putInt(FOOD, 0)
        editor.putInt(SHOP, 0)
        editor.putInt(TRAVEL, 0)
        editor.putInt(OTHERS, 0)
        editor.putInt(TOTAL, 0)

        editor.apply()
    }
}