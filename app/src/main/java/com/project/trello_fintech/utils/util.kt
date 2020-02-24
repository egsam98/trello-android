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

fun DatabaseReference.inc(onCompleteCallback: ((Int) -> Unit)? = null,
                          onErrorCallback: ((DatabaseError) -> Unit)? = null) {
    runTransaction(object: Transaction.Handler {
        override fun doTransaction(data: MutableData): Transaction.Result {
            val value = data.getValue(Int::class.java)
            data.value = (value?: 0) + 1
            return Transaction.success(data)
        }

        override fun onComplete(err: DatabaseError?, p1: Boolean, dataSnapshot: DataSnapshot?) {
            when {
                dataSnapshot != null && onCompleteCallback != null -> onCompleteCallback(dataSnapshot.getValue(Int::class.java)!!)
                err != null && onErrorCallback != null -> onErrorCallback(err)
            }
        }
    })
}

fun DatabaseReference.dec(onCompleteCallback: ((Int) -> Unit)? = null,
                          onErrorCallback: ((DatabaseError) -> Unit)? = null) {
    runTransaction(object: Transaction.Handler {
        override fun doTransaction(data: MutableData): Transaction.Result {
            val value = data.getValue(Int::class.java)
            data.value = if (value != null && value > 0) value - 1 else 0
            return Transaction.success(data)
        }

        override fun onComplete(err: DatabaseError?, p1: Boolean, dataSnapshot: DataSnapshot?) {
            when {
                err != null && onErrorCallback != null -> onErrorCallback(err)
                dataSnapshot != null && onCompleteCallback != null -> onCompleteCallback(dataSnapshot.getValue(Int::class.java)!!)
            }
        }
    })
}