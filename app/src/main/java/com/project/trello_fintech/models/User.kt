package com.project.trello_fintech.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Пользователь приложения
 * @property fullname String
 * @property avatar Bitmap?
 * @property avatarUrl String
 * @property initials String
 */
data class User(
        @SerializedName("fullName", alternate = ["name"]) val fullname: String = "",
        @SerializedName("id") val id: String = "",
        @SerializedName("initials") val initials: String = ""
    ): Serializable {

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null
}