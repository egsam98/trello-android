package com.project.trello_fintech.utils

import android.content.Context
import android.net.ConnectivityManager
import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import com.project.trello_fintech.Application
import java.util.*


fun Date?.toDefaultFormat(): String = when (this) {
    null -> "Нет данных"
    else -> toLocaleString()
}

fun Date?.toShortFormat(): String = when (this) {
    null -> "Нет данных"
    else -> DateFormat.format("yyyy-MM-dd hh:mm:ss", this).toString()
}

fun isInternetAvailable(): Boolean {
    val cm = Application.component.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo?.isConnected?: false
}

fun MutableLiveData<*>.update() {
    this.value = this.value
}