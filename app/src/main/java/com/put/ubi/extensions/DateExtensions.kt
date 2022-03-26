package com.put.ubi.extensions

import android.text.format.DateFormat
import java.util.*


fun getDate(timestamp: Long): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH).apply {
        timeInMillis = timestamp
    }
    return DateFormat.format("dd-MM-yyyy", calendar).toString()
}