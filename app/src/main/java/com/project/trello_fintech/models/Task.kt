package com.project.trello_fintech.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.random.Random

/**
 * Задача - элементарная структура (класс) проекта
 * @property text String
 * @property id Long
 */
data class Task(
        @SerializedName("name") val text: String = randomString(),
        @SerializedName("id") val id: String = ""
    ): Serializable

private fun randomString(): String {
    val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return (1..5)
        .map { Random.nextInt(0, charPool.length) }
        .map(charPool::get)
        .joinToString("")
}