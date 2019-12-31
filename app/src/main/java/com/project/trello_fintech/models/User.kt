package com.project.trello_fintech.models

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Пользователь приложения
 * @property fullname String
 * @property avatar Bitmap?
 * @property avatarUrl String
 * @property initials String
 */
data class User(
        @SerializedName("fullName", alternate = ["name"]) val fullname: String,
        @SerializedName("id") val id: String = "",
        @SerializedName("initials") val initials: String = ""
    ): Serializable {

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null

    fun injectImage(imageView: ImageView, onLoadFailed: () -> Unit) {
        onLoadFailed()
        return
        if (avatarUrl == null) {
            onLoadFailed()
            return
        }
        Glide.with(imageView)
            .asBitmap()
            .load(avatarUrl)
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    onLoadFailed()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

//    @SerializedName("avatarUrl")
//    val avatarUrl: String = ""
}