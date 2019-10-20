package com.project.trello_fintech.models

import android.graphics.Color
import java.io.Serializable
import kotlin.random.Random


/**
 * Модель "доска"
 * @property title String - название
 * @property color Int - идентификатор цвета
 * @property columns MutableList<String> - список колонок, содержащих задачи
 * @see Column
 */
data class Board(val title: String, val category: Category, val color: Int = randomColorId()): Serializable {

    enum class Category {
        PERSONAL_BOARDS {
            override fun toString() = "Personal boards"
        },
        WORK_BOARDS {
            override fun toString() = "Work boards"
        },
        OTHER_BOARDS {
            override fun toString() = "Other boards"
        }
    }

    val columns = mutableListOf(
        Column("TO DO"),
        Column("IN PROGRESS"),
        Column("DONE")
    )
}

private fun randomColorId(): Int {
    val (one, two, three) = IntArray(3) { Random.nextInt(256) }
    return Color.rgb(one, two, three)
}
