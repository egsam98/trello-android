package com.project.trello_fintech.utils

import android.content.SharedPreferences


/**
 * Репозиторий для доступа к SharedPreferences
 * @param T
 * @property preferences SharedPreferences?
 */
abstract class PreferencesRepository<T> {
    protected var preferences: SharedPreferences? = null

    fun attach(sharedPreferences: SharedPreferences){
        preferences = sharedPreferences
    }

    fun contains(key: String) = preferences?.contains(key)?: false
    fun delete(key: String) = preferences?.edit()?.remove(key)?.apply()

    abstract fun get(key: String, default: T? = null): T?
    abstract fun put(key: String, value: T)
}

/**
 * Реализация PreferencesRepository для хранения строк
 */
object StringsRepository: PreferencesRepository<String>() {
    override fun get(key: String, default: String?) = preferences?.getString(key, default)

    override fun put(key: String, value: String) {
        preferences?.edit()?.putString(key, value)?.apply()
    }
}