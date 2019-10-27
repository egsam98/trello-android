package com.project.trello_fintech.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Колонка со списком задач
 * @property id String
 * @property title String
 */
data class Column(
    @SerializedName("id") val id: String,
    @SerializedName("name") val title: String
    ): Serializable