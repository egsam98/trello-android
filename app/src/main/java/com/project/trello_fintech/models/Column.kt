package com.project.trello_fintech.models

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