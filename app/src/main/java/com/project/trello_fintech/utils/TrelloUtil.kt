package com.project.trello_fintech.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.webkit.URLUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.project.trello_fintech.api.ChecklistApi
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskApi
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.Checklist
import com.project.trello_fintech.models.Task
import io.reactivex.rxkotlin.flatMapIterable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


private const val TASK_HOSTNAME = "trello.com"

@Singleton
class TrelloUtil @Inject constructor(
    private val cxt: Context,
    private val retrofitClient: RetrofitClient) {

    private val checklistRetrofit by lazy {
        retrofitClient.create<ChecklistApi>(scheduler = Schedulers.io())
    }

    /**
     * Является ли URL задачи валидным
     * @param checkitem Item
     * @return Boolean
     */
    fun isTaskURL(checkitem: Checklist.Item): Boolean {
        val url = checkitem.title
        return URLUtil.isValidUrl(url) && TASK_HOSTNAME in url
    }

    /**
     * Метод возвращает ID и текст задачи по ее URL
     * @param url String
     * @return Pair<String, String> ID и текст задачи
     */
    fun parseTaskUrl(url: String): Pair<String, String> {
        val id = url.substringAfterLast('/')
        val retrofit = retrofitClient.create<TaskApi>(scheduler = Schedulers.io())
        return retrofit.findById(id)
            .map { it.id to it.text }
            .blockingGet()
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

    /**
     * Процент заверешенности задачи
     * @param task Task
     * @return Int
     */
    fun getProgressPercent(task: Task): Int {
        return checklistRetrofit.findAllByTaskId(task.id).toObservable()
            .flatMapIterable()
            .map { it.items.toList() }
            .reduce { t1, t2 -> t1 + t2 }
            .map {
                val checkedCount = it.sumBy { item -> if (item.isChecked) 1 else 0 }
                (checkedCount.toFloat() / it.size.toFloat() * 100).toInt()
            }
            .blockingGet(0)
    }

    /**
     * Зависимые задачи от выбранной
     * @param task Task
     * @return List<String>
     */
    fun getDependants(task: Task): List<String> {
        return checklistRetrofit.findAllByTaskId(task.id).toObservable()
            .flatMapIterable()
            .flatMapIterable {
                it.items.map { item ->
                    if (isTaskURL(item))
                        parseTaskUrl(item.title).first
                    else
                        ""
                }
            }
            .filter { it.isNotEmpty() }
            .toList()
            .blockingGet()
    }
}