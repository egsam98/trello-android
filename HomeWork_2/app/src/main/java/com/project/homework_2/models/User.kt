package com.project.homework_2.models

import android.graphics.Bitmap

/**
 * Пользователь приложения
 * @property fullname String
 * @property avatar Bitmap?
 */
data class User(val fullname: String, val avatar: Bitmap? = null)