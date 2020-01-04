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
 * @property trelloPos String
 * @property description String
 * @property boardId String
 * @property creationDate Date
 * @property dueDate Date?
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

    @SerializedName("idBoard")
    val boardId: String = ""

    var creationDate: Date = Date()

    @SerializedName("due")
    val dueDate: Date? = null

    var attachments: List<Attachment> = listOf()

    enum class AttachmentType {
        SMALL, MEDIUM, LARGE
    }

    /**
     * Вложение к задаче
     * @property id String
     * @property name String
     * @property mimeType String?
     * @property date Date
     * @property imageUrls Array<ImageUrl>?
     * @property url String
     */
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

    /**
     * История изменений задачи
     * @property data Data
     * @property type String
     * @property date Date
     * @property creator User
     * @property message Spanned
     */
    data class History(
        @SerializedName("data") val data: Data,
        @SerializedName("type") val type: String,
        @SerializedName("date") val date: Date,
        @SerializedName("memberCreator") val creator: User
    ) {

        companion object {
            const val TYPES =
                "addAttachmentToCard," +
                "createCard," +
                "addMemberToCard," +
                "updateCard:desc," +
                "convertToCardFromCheckItem"
        }

        /**
         * Измененные данные задачи
         * @property task Task?
         * @property attachment Attachment?
         * @property member User?
         */
        data class Data(
            @SerializedName("card") val task: Task?,
            @SerializedName("attachment") val attachment: Attachment?,
            @SerializedName("member") val member: User?
        )

        val message : Spanned
            get() {
                val creatorText = "<b>${creator.fullname}</b>"
                val html = when {
                    type == "createCard" -> "$creatorText создал(-а) задачу"
                    type == "convertToCardFromCheckItem" ->
                        "$creatorText сконвертировал(-а) задачу из элемента чек-листа задачи <b>${data.task?.text}</b>"
                    type == "updateCard" && data.task?.description != null ->
                        "$creatorText обновил(-а) описание задачи: <b>${data.task.description}</b>"
                    type == "addMemberToCard" -> "$creatorText добавил(-а) нового участника <b>${data.member?.fullname}</b>"
                    type == "addAttachmentToCard" -> "$creatorText добавил(-а) вложение <b>${data.attachment?.name}</b>"
                    else -> throw IllegalArgumentException("Неизвестный тип истории изменения")
                }
                return Html.fromHtml(html)
            }
    }
}

// TODO: move to utils
private fun randomString(): String {
    val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return (1..5)
        .map { Random.nextInt(0, charPool.length) }
        .map(charPool::get)
        .joinToString("")
}