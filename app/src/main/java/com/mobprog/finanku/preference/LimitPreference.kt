package com.mobprog.finanku.preference

import android.content.Context
import com.mobprog.finanku.data.Limit

internal class LimitPreference(context: Context) {
    companion object {
        private const val PREFS_NAME = "limit_pref"
        private const val ID = "id"
        private const val FOOD = "food"
        private const val SHOP = "shop"
        private const val TRAVEL = "travel"
        private const val OTHERS = "others"
        private const val TOTAL = "total"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setLimit(id: Int, food: Int, shop: Int, travel: Int, others: Int, total: Int) {
        val editor = preferences.edit()
        editor.putInt(ID, id)
        editor.putInt(FOOD, food)
        editor.putInt(SHOP, shop)
        editor.putInt(TRAVEL, travel)
        editor.putInt(OTHERS, others)
        editor.putInt(TOTAL, total)

        editor.apply()
    }

    fun getLimit(): Limit {
        return Limit(
            preferences.getInt(ID, 0),
            preferences.getInt(FOOD, 0),
            preferences.getInt(SHOP, 0),
            preferences.getInt(TRAVEL, 0),
            preferences.getInt(OTHERS, 0),
            preferences.getInt(TOTAL, 0),
        )
    }

    fun clearLimit() {
        val editor = preferences.edit()
        editor.putInt(ID, 0)
        editor.putInt(FOOD, 0)
        editor.putInt(SHOP, 0)
        editor.putInt(TRAVEL, 0)
        editor.putInt(OTHERS, 0)
        editor.putInt(TOTAL, 0)

        editor.apply()
    }
}