package com.project.trello_fintech.models

import java.io.Serializable
import java.util.*
import kotlin.random.Random

/**
 * Задача - элементарная структура (класс) проекта
 * @property text String
 * @property id Long
 */
data class Task(val text: String = randomString()): Serializable {
    val id: Long = Calendar.getInstance().timeInMillis
}

private fun randomString(): String {
    val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return (1..5)
        .map { Random.nextInt(0, charPool.length) }
        .map(charPool::get)
        .joinToString("")
}