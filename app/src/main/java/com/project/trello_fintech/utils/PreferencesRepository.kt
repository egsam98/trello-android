package com.project.trello_fintech.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


/**
 * Репозиторий для доступа к SharedPreferences
 * @param T
 * @property preferences SharedPreferences?
 */
abstract class PreferencesRepository<T>(context: Context) {
    protected val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun contains(key: String) = preferences.contains(key)
    fun delete(key: String) = preferences.edit().remove(key).apply()

    abstract fun get(key: String, default: T? = null): T?
    abstract fun put(key: String, value: T)
}

/**
 * Реализация PreferencesRepository для хранения строк
 */
class StringsRepository(context: Context): PreferencesRepository<String>(context) {
    override fun get(key: String, default: String?): String? = preferences.getString(key, default)

    override fun put(key: String, value: String) {
        preferences.edit().putString(key, value)?.apply()
    }
}