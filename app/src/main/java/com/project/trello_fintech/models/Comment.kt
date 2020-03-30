package com.project.trello_fintech.models

import java.util.*

/**
 * Комментарий к задаче
 * @property author User?
 * @property text String
 * @property date Date
 */
data class Comment(val author: User?, val text: String) {
    val date: Date = Date()
    constructor(): this(null, "") // Необходим конструктор без параметров для работы с Firebase Firestore
}