package com.project.trello_fintech.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskApi
import com.project.trello_fintech.models.Board
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TrelloUtil @Inject constructor(
    private val cxt: Context,
    private val retrofitClient: RetrofitClient) {

    /**
     * Метод возвращает ID и текст задачи по ее URL
     * @param url String
     * @return Pair<String, String> ID и текст задачи
     */
    fun parseTaskUrl(url: String): Pair<String, String> {
        val id = url.substringAfterLast('/')
        val retrofit = retrofitClient.create<TaskApi>(scheduler = Schedulers.io())
        val text = retrofit.findById(id)
            .map { it.text }
            .blockingGet()
        return Pair(id, text)
    }

    /**
     * Загрузка фонового изображения доски в указанный View
     * @param board Board
     * @param intoView View
     */
    fun loadBoardBackground(board: Board, intoView: View) {
        val loadObject = board.prefs?.imageUrls?.last()?.url?: board.prefs?.fromHexColor()
        Glide.with(cxt)
            .asBitmap()
            .load(loadObject)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val drawable = BitmapDrawable(cxt.resources, resource)
                    intoView.background = drawable
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}