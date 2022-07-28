package com.mobprog.finanku.utils

import java.text.SimpleDateFormat
import java.util.*

fun dateNow(): String {
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(Date())
}

fun formatDate(stringDate: String): String {
    val baseFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val stringDatetoDate: Date? = baseFormat.parse(stringDate)
    return outputFormat.format(stringDatetoDate ?: Date())
}

fun previousMonth(): String {
    val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, -1)

    return format.format(cal.time)
}