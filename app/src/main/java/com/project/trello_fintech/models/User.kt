package com.project.trello_fintech.models

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

/**
 * Пользователь приложения
 * @property fullname String
 * @property avatar Bitmap?
 * @property avatarUrl String
 * @property initials String
 */
data class User(
        @SerializedName("fullName", alternate = ["name"]) val fullname: String,
        val avatar: Bitmap? = null
    ) {
    @SerializedName("avatarUrl")
    val avatarUrl: String = ""

    @SerializedName("initials")
    val initials: String = ""
}