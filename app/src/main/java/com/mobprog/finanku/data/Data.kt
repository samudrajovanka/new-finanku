package com.mobprog.finanku.data

import android.content.Context
import com.mobprog.finanku.model.*
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.preference.LimitPreference

class Data {
    companion object {
        val categories = ArrayList<CategoryResponse>()
        fun getCategoryByName(name: String): CategoryResponse {
            val categoryFilter = categories.filter { category -> category.category?.type == name }

            return categoryFilter[0]
        }

        fun getStatistics(context: Context): ArrayList<Statistic> {
            val expensePref = ExpensesPreference(context)
            val limitPreference = LimitPreference(context)

            return arrayListOf(
                Statistic(
                    CategoryExpenses.FOOD,
                    expensePref.getFood(),
                    limitPreference.getLimit().food
                ),
                Statistic(
                    CategoryExpenses.SHOP,
                    expensePref.getShop(),
                    limitPreference.getLimit().shop
                ),
                Statistic(
                    CategoryExpenses.TRAVEL,
                    expensePref.getTravel(),
                    limitPreference.getLimit().travel
                ),
                Statistic(
                    CategoryExpenses.OTHERS,
                    expensePref.getOthers(),
                    limitPreference.getLimit().others
                ),
            )
        }

        fun getLastStatistic(): ArrayList<Statistic> {
            return arrayListOf<Statistic>(
                Statistic(
                    CategoryExpenses.FOOD,
                    1100000,
                    2000000
                ),
                Statistic(
                    CategoryExpenses.SHOP,
                    4559000,
                    5000000
                ),
                Statistic(
                    CategoryExpenses.TRAVEL,
                    997500,
                    1500000
                ),
                Statistic(
                    CategoryExpenses.OTHERS,
                    550000,
                    1500000
                )
            )
        }
    }

}