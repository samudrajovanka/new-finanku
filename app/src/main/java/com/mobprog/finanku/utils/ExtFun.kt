package com.mobprog.finanku.utils

import android.content.Context
import java.text.NumberFormat
import java.util.*

fun Int.toCurrencyIDR(prefix: Boolean = true): String {
    val localeID = Locale("in", "ID")
    val format: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("IDR")

    if (prefix) {
        return String.format("Rp %s", format.format(this).drop(2))
    }

    return format.format(this).drop(2)
}

fun Int.toPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}
