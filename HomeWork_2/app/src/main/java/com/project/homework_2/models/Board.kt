package com.project.homework_2.models

import java.io.Serializable


/**
 * Модель "доска"
 * @property title String - название
 * @property color Int - идентификатор цвета
 */
data class Board(val title: String, val color: Int): Serializable
