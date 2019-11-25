package com.project.trello_fintech.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * URL изображения
 * @property url String
 */
data class ImageUrl(@SerializedName("url") val url: String): Serializable