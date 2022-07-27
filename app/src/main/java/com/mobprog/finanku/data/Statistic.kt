package com.mobprog.finanku.data

import com.mobprog.finanku.model.CategoryExpenses
import kotlin.math.roundToInt

data class Statistic(
    var category: CategoryExpenses,
    var spent: Int,
    var limit: Int,
) {
    fun getPercent(): Int {
        val percent = ((spent.toDouble() / limit) * 100).roundToInt()

        return if (percent <= 100) percent else 100
    }

    companion object {
        fun getStatus(percent: Int): String {
            if (percent > 80) {
                return StatusExpenses.WASTEFUL.status
            } else if (percent > 50) {
                return StatusExpenses.WATCH_OUT.status
            }

            return StatusExpenses.GOOD.status
        }
    }
}