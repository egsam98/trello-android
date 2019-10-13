package com.project.homework_2.models

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
data class Board(val title: String, val color: Int = randomColorId()): Serializable {
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
