package com.project.trello_fintech.models

import android.graphics.Bitmap
import android.graphics.Color
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Модель "доска"
 * @property title String название
 * @property category Category? категория доски (используется для группировки)
 * @property id String
 * @property columns List<Column> список колонок, содержащих задачи
 * @property prefs Prefs? Preferences доски
 */
data class Board(
    @SerializedName("name") val title: String,
    @SerializedName("organization") var category: Category?,
    @SerializedName("id") val id: String = ""
    ): Serializable, IListItem {

    var columns: List<Column> = listOf()

    @SerializedName("prefs")
    val prefs: Prefs? = null

    override fun getType() = IListItem.BODY


    data class Category(
        @SerializedName("displayName") private val title: String
    ): IListItem, Serializable, Comparable<Category> {

        companion object {
            fun default() = Category(title = "Персональные доски")
        }

        @SerializedName("id")
        val id: String = ""

        override fun getType() = IListItem.HEADER
        override fun toString() = title
        override fun compareTo(other: Category) = title.compareTo(other.title)
    }

    /**
     * Preferences для доски
     * @property hexColor String фоновый цвет доски
     * @property imageUrls Array<ImageUrl>? ссылки на фоновые изображения доски (различные размеры)
     * @constructor
     */
    data class Prefs(
        @SerializedName("backgroundColor") val hexColor: String,
        @SerializedName("backgroundImageScaled") val imageUrls: Array<ImageUrl>?
    ): Serializable {

        fun fromHexColor(): Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.parseColor(hexColor))
        }
    }
}
