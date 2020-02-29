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
 * @property data Data
 * @property to String
 */
class FirebaseMessage(
    @SerializedName("to")  val to: String,
    @SerializedName("data") val data: Data) {

    companion object {
        private const val FROM_ID_KEY = "fromId"
        private const val BOARD_ID_KEY = "boardId"
        private const val NOTIFICATION_TYPE_KEY = "notificationType"
        private const val TITLE_KEY = "title"
        private const val BODY_KEY = "body"

        fun parseData(msg: RemoteMessage): Data? {
            val isAllPresent = arrayOf(FROM_ID_KEY, BOARD_ID_KEY, NOTIFICATION_TYPE_KEY, TITLE_KEY, BODY_KEY).all {
                msg.data[it] != null
            }
            if (isAllPresent) with (msg.data) {
                val notificationType =
                    try {
                        NotificationType.valueOf(getValue(NOTIFICATION_TYPE_KEY))
                    } catch (e: IllegalArgumentException) {
                        NotificationType.MESSAGE
                    }
                return Data(getValue(FROM_ID_KEY), notificationType, getValue(BOARD_ID_KEY), getValue(TITLE_KEY), getValue(BODY_KEY))
            }
            return null
        }
    }

    data class Data(
        @SerializedName(FROM_ID_KEY) val fromId: String,
        @SerializedName(NOTIFICATION_TYPE_KEY) val notificationType: NotificationType,
        @SerializedName(BOARD_ID_KEY) val boardId: String,
        @SerializedName(TITLE_KEY) val title: String,
        @SerializedName(BODY_KEY) val body: String
    )
}