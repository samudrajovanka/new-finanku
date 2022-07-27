package com.mobprog.finanku.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

fun dateNow(): String {
    return formatDate(Date())
}

fun formatDate(date: Date): String {
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(date)
}

fun previousMonth(): String {
    val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, -1)

    return format.format(cal.time)
}