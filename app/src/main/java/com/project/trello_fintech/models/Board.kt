package com.project.trello_fintech.models

import android.graphics.Color
import java.io.Serializable
import kotlin.random.Random


/**
 * Модель "доска"
 * @property title String - название
 * @property category Board.Category - категория доски (используется для группировки)
 * @property color Int - идентификатор цвета
 * @property columns MutableList<String> - список колонок, содержащих задачи
 * @see Column
 */
data class Board(val title: String, var category: Category, val color: Int = randomColorId()): Serializable, IListItem() {

    data class Category(private val title: String): IListItem(), Serializable, Comparable<Category> {
        override fun getType() = HEADER
        override fun toString() = title
        override fun compareTo(other: Category) = title.compareTo(other.title)
    }

    val columns = mutableListOf(
        Column("TO DO"),
        Column("IN PROGRESS"),
        Column("DONE")
    )

    override fun getType() = BODY
}

private fun randomColorId(): Int {
    val (one, two, three) = IntArray(3) { Random.nextInt(256) }
    return Color.rgb(one, two, three)
}
