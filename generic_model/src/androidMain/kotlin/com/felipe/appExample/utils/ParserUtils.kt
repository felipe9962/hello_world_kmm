package com.felipe.appExample.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object ParserUtils {

    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    fun parseTimeToTimestamp(time: Long?): String? {
        if (time == null)
            return null
        val date = Date(time)
        return simpleDateFormat.format(date)
    }

    fun parseTimestampToLong(timestamp: String?): Long? {
        if (timestamp == null)
            return null

        return try {
            simpleDateFormat.parse(timestamp).time
        } catch (ex: Exception) {
            null
        }
    }
}