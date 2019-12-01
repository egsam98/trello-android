package com.project.trello_fintech.utils

import android.graphics.Color
import kotlin.random.Random

fun randomColor(): Int {
    val c = { Random.nextInt(256) }
    return Color.rgb(c(), c(), c())
}