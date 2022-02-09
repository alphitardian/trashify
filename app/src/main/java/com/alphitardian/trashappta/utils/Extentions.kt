package com.alphitardian.trashappta.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.formatDate(): String {
    val localDate = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
    return DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm").format(localDate)
}