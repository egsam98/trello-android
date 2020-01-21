package com.project.trello_fintech.models

import com.google.gson.annotations.SerializedName

/**
 * Перечень действий для выполнения для одной конкретной задачи
 * @property title String
 * @property items List<Item>
 */
data class Checklist(
    @SerializedName("name") val title: String,
    @SerializedName("checkItems") val items: List<Item>
) {

    data class Item(
        @SerializedName("name") val title: String,
        @SerializedName("state") private val state: String
    ) {

        val isChecked: Boolean
            get() = state == "complete"
    }
}