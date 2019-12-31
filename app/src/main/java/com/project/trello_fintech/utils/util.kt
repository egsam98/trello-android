package com.project.trello_fintech.utils

import android.text.format.DateFormat
import java.util.*


fun Date?.toDefaultFormat(): String = when (this) {
    null -> "Нет данных"
    else -> toLocaleString()
}

fun Date?.toShortFormat(): String = when (this) {
    null -> "Нет данных"
    else -> DateFormat.format("dd.MM.yyyy hh:mm:ss", this).toString()
}