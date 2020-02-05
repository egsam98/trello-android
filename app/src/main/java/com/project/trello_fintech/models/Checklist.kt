package com.project.trello_fintech.models

import com.google.gson.annotations.SerializedName

/**
 * Перечень действий для выполнения для одной конкретной задачи
 * @property id String
 * @property title String
 * @property items MutableList<Item>
 */
data class Checklist(
    @SerializedName("id") val id: String,
    @SerializedName("name") var title: String,
    @SerializedName("checkItems") val items: MutableList<Item>
) {

    /**
     * Элемент чек-листа
     * @property id String
     * @property title String
     * @property state String
     * @property isChecked Boolean
     */
    data class Item(
        @SerializedName("id") val id: String,
        @SerializedName("name") var title: String,
        @SerializedName("state") private val state: String
    ) {

        val isChecked: Boolean
            get() = state == "complete"
    }
}