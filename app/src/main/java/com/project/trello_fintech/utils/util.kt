package com.project.trello_fintech.utils

import android.content.Context
import android.net.ConnectivityManager
import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
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

infix fun <T> MutableLiveData<MutableList<T>>.add(elem: T) {
    value?.add(elem)
    update()
}

infix fun <T> MutableLiveData<MutableList<T>>.remove(elem: T) {
    value?.remove(elem)
    update()
}

// TODO: test in next commit
fun DatabaseReference.inc(onCompleteCallback: ((DataSnapshot) -> Unit)? = null) {
    runTransaction(object : Transaction.Handler {
        override fun doTransaction(mutableData: MutableData): Transaction.Result {
            val value = mutableData.getValue(Int::class.java)
            if (value == null)
                mutableData.value = 0
            else
                mutableData.value = value + 1
            return Transaction.success(mutableData)
        }

        override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot?) {
            dataSnapshot?.let { onCompleteCallback?.invoke(it) }
        }
    })
}

fun DatabaseReference.dec(onCompleteCallback: ((DataSnapshot) -> Unit)? = null) {
    runTransaction(object : Transaction.Handler {
        override fun doTransaction(mutableData: MutableData): Transaction.Result {
            val value = mutableData.getValue(Int::class.java)
            if (value != null && value != 0)
                mutableData.value = value - 1
            return Transaction.success(mutableData)
        }

        override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot?) {
            dataSnapshot?.let { onCompleteCallback?.invoke(it) }
        }
    })
}