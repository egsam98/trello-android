package com.project.trello_fintech.models

import com.google.gson.annotations.SerializedName

/**
 * Перечень действий для выполнения для одной конкретной задачи
 * @property title String
 * @property pos Int
 * @property items List<Item>
 */
data class Checklist(
    @SerializedName("name") val title: String,
    @SerializedName("pos") val pos: Int,
    @SerializedName("checkItems") val items: List<Item>
): IListItem {

    data class Item(
        @SerializedName("name") val title: String,
        @SerializedName("state") private val state: String,
        @SerializedName("pos") val pos: Int
    ): IListItem {

        val isChecked: Boolean
            get() = state == "complete"

        override fun getType(): Int = IListItem.BODY
    }

    override fun getType(): Int = IListItem.HEADER
}