package com.project.trello_fintech.models.firebase

import com.google.firebase.messaging.RemoteMessage
import com.google.gson.annotations.SerializedName
import com.project.trello_fintech.services.utils.NotificationType
import java.lang.IllegalArgumentException


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
 * @property from String
 * @property data Data
 * @property to String
 */
class FirebaseMessage private constructor(
        @SerializedName("from") val from: String,
        @SerializedName("data") val data: Data
    ){

    companion object {
        const val TOPIC_NAME = "/topics/."
        private const val BOARD_ID_KEY = "boardId"
        private const val NOTIFICATION_TYPE_KEY = "notificationType"
        private const val TITLE_KEY = "title"
        private const val BODY_KEY = "body"

        fun create(from: String, title: String, boardId: String, body: String,
                   notificationType: NotificationType): FirebaseMessage {
            val data = Data(notificationType, boardId, title, body)
            return FirebaseMessage(from, data)
        }

        fun parseData(msg: RemoteMessage): Data? {
            val isAllPresent = arrayOf(BOARD_ID_KEY, NOTIFICATION_TYPE_KEY, TITLE_KEY, BODY_KEY).all {
                msg.data[it] != null
            }
            if (isAllPresent) with (msg.data) {
                val notificationType =
                    try {
                        NotificationType.valueOf(getValue(NOTIFICATION_TYPE_KEY))
                    } catch (e: IllegalArgumentException) {
                        NotificationType.MESSAGE
                    }
                return Data(notificationType, getValue(BOARD_ID_KEY), getValue(TITLE_KEY), getValue(BODY_KEY))
            }
            return null
        }
    }

    @SerializedName("to")
    private val to: String = TOPIC_NAME

    data class Data(
        @SerializedName(NOTIFICATION_TYPE_KEY) val notificationType: NotificationType,
        @SerializedName(BOARD_ID_KEY) val boardId: String,
        @SerializedName(TITLE_KEY) val title: String,
        @SerializedName(BODY_KEY) val body: String
    )
}