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
        @SerializedName("state") private var state: String
    ) {
        companion object {
            const val COMPLETE = "complete"
            const val INCOMPLETE = "incomplete"
            fun stateOf(isChecked: Boolean): String {
                return if (isChecked) COMPLETE else INCOMPLETE
            }
        }

        val isChecked: Boolean
            get() = state == COMPLETE

        fun setState(isChecked: Boolean) {
            state = stateOf(isChecked)
        }
    }
}