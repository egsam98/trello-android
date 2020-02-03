package com.project.trello_fintech.utils

import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskApi
import io.reactivex.schedulers.Schedulers


class TrelloUtil(private val retrofitClient: RetrofitClient) {

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
}