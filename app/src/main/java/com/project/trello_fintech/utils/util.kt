package com.project.trello_fintech.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.text.format.DateFormat
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.project.trello_fintech.Application
import java.lang.Exception
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

fun DocumentReference.setField(key: String, value: Any, merge: Boolean = true) {
    if (merge)
        set(mapOf(key to value), SetOptions.merge()).addOnFailureListener { it.show() }
    else
        set(mapOf(key to value)).addOnFailureListener { it.show() }
}

fun DocumentReference.incFields(vararg fieldName: String, value: Long = 1) {
    val updates = fieldName.associate { Pair(it, FieldValue.increment(value)) }
    update(updates).addOnFailureListener { it.show() }
}

fun DocumentReference.decFields(vararg fieldName: String, value: Long = 1) {
    val updates = fieldName.associate { Pair(it, FieldValue.increment(-value)) }
    update(updates).addOnFailureListener { it.show() }
}

fun DocumentReference.deleteFields(vararg fieldName: String) {
    val updates = fieldName.associate { Pair(it, FieldValue.delete()) }
    update(updates).addOnFailureListener { it.show() }
}

fun Exception.show() {
    Toast.makeText(Application.component.context, message?: localizedMessage, Toast.LENGTH_LONG).show()
}

fun Resources.getBitmap(id: Int): Bitmap = BitmapFactory.decodeResource(this, id)