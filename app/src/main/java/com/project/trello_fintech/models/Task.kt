package com.project.trello_fintech.models

import android.text.Html
import android.text.Spanned
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.random.Random

/**
 * Задача - элементарная структура (класс) проекта
 * @property text String
 * @property id String
 * @property trelloPos String позиция в списке trello, представляет число с плавающей точкой либо "bottom", "top"
 * @property attachments List<Attachment>
 */
data class Task(
        @SerializedName("name") val text: String = randomString(),
        @SerializedName("id") val id: String = ""
    ): Serializable {

    @SerializedName("pos")
    var trelloPos: String = "bottom"

    @SerializedName("desc")
    var description: String = ""

    var attachments: List<Attachment> = listOf()

    enum class AttachmentType {
        SMALL, MEDIUM, LARGE
    }

    data class Attachment(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("mimeType") private val mimeType: String?,
        @SerializedName("date") val date: Date,
        @SerializedName("previews") private val imageUrls: Array<ImageUrl>?,
        @SerializedName("url") val url: String
    ): Serializable {

        fun isImage() = mimeType?.startsWith("image/")?: false

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

    data class History(
        @SerializedName("data") val data: Data,
        @SerializedName("type") val type: String,
        @SerializedName("date") val date: Date,
        @SerializedName("memberCreator") val creator: User
    ) {

        data class Data(
            @SerializedName("card") val task: Task?,
            @SerializedName("attachment") val attachment: Attachment?,
            @SerializedName("member") val member: User?
        )

        val message : Spanned
            get() {
                val creatorText = "<b>${creator.fullname}</b>"
                val html = when {
                    data.attachment != null -> "$creatorText добавил вложение <b>${data.attachment.name}</b>"
                    data.member != null -> "$creatorText добавил нового участника <b>${data.member.fullname}</b>"
                    data.task != null -> "$creatorText обновил описание задачи: <b>${data.task.description}</b>"
                    else -> throw IllegalArgumentException("Неизвестный тип истории изменения")
                }
                return Html.fromHtml(html)
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