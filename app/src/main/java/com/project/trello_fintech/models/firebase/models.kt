package com.project.trello_fintech.models.firebase

import com.google.gson.annotations.SerializedName


/**
 * Данные о сессии потокового видео/аудио
 * @property apiKey String
 * @property sessionId String
 * @property token String
 */
class SessionStart {
    @SerializedName("apiKey")
    val apiKey: String = ""

    @SerializedName("sessionId")
    val sessionId: String = ""

    @SerializedName("token")
    val token: String = ""
}

/**
 * Cloud Message структура
 * @property data Data
 * @property to String
 */
class FirebaseMessage private constructor(
        @SerializedName("notification") val data: Data
    ){

    companion object {
        const val TOPIC_NAME = "/topics/."
        fun create(title: String, body: String): FirebaseMessage {
            return FirebaseMessage(Data(title, body))
        }
    }

    @SerializedName("to")
    private val to: String = TOPIC_NAME

    data class Data(
        @SerializedName("title") val title: String,
        @SerializedName("body") val body: String
    )
}