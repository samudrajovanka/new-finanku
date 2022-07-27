package com.mobprog.finanku.data

import android.content.Context
import com.mobprog.finanku.model.*
import com.mobprog.finanku.model.User
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.preference.LimitPreference

class Data {
    companion object {
        val categories = ArrayList<CategoryResponse>()
        fun getCategoryByName(name: String): CategoryResponse {
            val categoryFilter = categories.filter { category -> category.category?.type == name }

            return categoryFilter[0]
        }

        private val user = User(
            "Robert", "Samuel", "testemail@gmail.com", "123456789", arrayListOf(
                Limit(CategoryExpenses.FOOD, 500000),
                Limit(CategoryExpenses.SHOP, 1000000),
                Limit(CategoryExpenses.TRAVEL, 500000),
                Limit(CategoryExpenses.OTHER, 800000),
            )
        )


        fun getHistory(): ArrayList<History> {
            return arrayListOf<History>(
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.FOOD),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.TRAVEL),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.FOOD),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.FOOD),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.OTHER),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.OTHER),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.TRAVEL),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.SHOP),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.SHOP),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.FOOD),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.FOOD),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.FOOD),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.SHOP),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.FOOD),
                History("20 March 2022", "Eat pizza with friend", 55000, CategoryExpenses.FOOD)
            )
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
                    CategoryExpenses.OTHER,
                    expensePref.getOthers(),
                    limitPreference.getLimit().others
                ),
            )
        }

        fun getStatistic(): ArrayList<Statistic> {
            return arrayListOf<Statistic>(
                Statistic(
                    CategoryExpenses.FOOD,
                    350000,
                    getUser().getLimitByType(CategoryExpenses.FOOD).total
                ),
                Statistic(
                    CategoryExpenses.SHOP,
                    20000,
                    getUser().getLimitByType(CategoryExpenses.SHOP).total
                ),
                Statistic(
                    CategoryExpenses.TRAVEL,
                    225000,
                    getUser().getLimitByType(CategoryExpenses.TRAVEL).total
                ),
                Statistic(
                    CategoryExpenses.OTHER,
                    20000,
                    getUser().getLimitByType(CategoryExpenses.OTHER).total
                )
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
                    CategoryExpenses.OTHER,
                    550000,
                    1500000
                )
            )
        }

        fun getUser(): User {
            return user
        }

        fun getTotalSpent(statistics: ArrayList<Statistic>): Int {
            var total = 0
            for (statistic: Statistic in statistics) {
                total += statistic.spent
            }

            return total
        }
    }

}