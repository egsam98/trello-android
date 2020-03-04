package com.project.trello_fintech.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.text.format.DateFormat
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch
import com.project.trello_fintech.Application
import com.project.trello_fintech.R
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.random.Random


fun randomString(length: Int): String {
    val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return (1..length)
        .map { Random.nextInt(0, charPool.length) }
        .map(charPool::get)
        .joinToString("")
}

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

fun DocumentReference.setField(key: String, value: Any, merge: Boolean = true): Task<Void> {
    return if (merge)
        set(mapOf(key to value), SetOptions.merge()).addOnFailureListener { it.show() }
    else
        set(mapOf(key to value)).addOnFailureListener { it.show() }
}

fun WriteBatch.setField(document: DocumentReference, key: String, value: Any, merge: Boolean = true): WriteBatch {
    return if (merge)
        set(document, mapOf(key to value), SetOptions.merge())
    else
        set(document, mapOf(key to value))
}

fun DocumentReference.incFields(vararg fieldName: String, value: Long = 1): Task<Void> {
    val updates = fieldName.associate { Pair(it, FieldValue.increment(value)) }
    return update(updates).addOnFailureListener { it.show() }
}

fun DocumentReference.decFields(vararg fieldName: String, value: Long = 1): Task<Void> {
    val updates = fieldName.associate { Pair(it, FieldValue.increment(-value)) }
    return update(updates).addOnFailureListener { it.show() }
}

fun DocumentReference.deleteFields(vararg fieldName: String): Task<Void> {
    val updates = fieldName.associate { Pair(it, FieldValue.delete()) }
    return update(updates).addOnFailureListener { it.show() }
}

fun Exception.show() {
    Toast.makeText(Application.component.context, message?: localizedMessage, Toast.LENGTH_LONG).show()
}

fun Resources.getBitmap(id: Int): Bitmap = BitmapFactory.decodeResource(this, id)

fun Resources.getVCSLogo(url: URL): Drawable? {
    val urlHost = url.host
    val id = when {
        "github" in urlHost -> R.drawable.github_logo_icon
        "bitbucket" in urlHost -> R.drawable.bitbucket_logo_icon
        "mercurial" in urlHost -> R.drawable.mercurial_logo_icon
        "gitlab" in urlHost -> R.drawable.gitlab_logo_icon
        "microsoft" in urlHost -> R.drawable.azure_logo_icon
        else -> null
    }
    return id?.let { getDrawable(it, null) }
}

fun Resources.getVCSLogo(urlPath: String): Drawable? {
    var urlFullPath = urlPath
    if (!urlPath.contains("http")) {
        urlFullPath = "http://$urlPath"
    }
    return try { getVCSLogo(URL(urlFullPath)) }
        catch (e: MalformedURLException) { null }
}

fun <T> LiveData<T>.observe(owner: LifecycleOwner, func: (T) -> Unit) =
    observe(owner, androidx.lifecycle.Observer(func))