package com.project.homework_2.models

import java.io.Serializable

/**
 * Колонка со списком задач
 * @property title String
 * @property tasks MutableList<Task>
 * @constructor
 */
data class Column(val title: String): Serializable {
    val tasks = mutableListOf<Task>()
}