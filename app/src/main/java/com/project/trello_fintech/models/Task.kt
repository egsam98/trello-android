package com.project.trello_fintech.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import kotlin.random.Random

/**
 * Задача - элементарная структура (класс) проекта
 * @property text String
 * @property id String
 * @property attachments List<Attachment>
 */
data class Task(
        @SerializedName("name") val text: String = randomString(),
        @SerializedName("id") val id: String = ""
    ): Serializable {

    var attachments: List<Attachment> = listOf()

    enum class AttachmentType {
        SMALL, MEDIUM, LARGE
    }

    data class Attachment(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("mimeType") private val mimeType: String,
        @SerializedName("date") val date: Date,
        @SerializedName("previews") private val imageUrls: Array<ImageUrl>?,
        @SerializedName("url") val url: String
    ): Serializable {

        fun isImage() = mimeType.startsWith("image/")

        fun getImageUrl(attachmentType: AttachmentType): String? {
            require(isImage()) {
                "Вложение не является изображением"
            }
            return when (attachmentType) {
                AttachmentType.SMALL -> imageUrls?.get(2)?.url
                AttachmentType.MEDIUM -> imageUrls?.get(3)?.url
                AttachmentType.LARGE -> url
            }
        }
    }
}

private fun randomString(): String {
    val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return (1..5)
        .map { Random.nextInt(0, charPool.length) }
        .map(charPool::get)
        .joinToString("")
}