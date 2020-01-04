package com.project.trello_fintech.api

import com.project.trello_fintech.models.Task
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*


interface TaskHistoryApi {

    @GET("cards/{id}/actions?filter=${Task.History.TYPES}")
    fun findAllByTaskId(@Path("id") id: String): Single<List<Task.History>>

    @GET("cards/{id}/actions")
    fun findAllByTypeAndTaskId(@Path("id") taskId: String,
                               @Query("filter") type: String): Single<List<Task.History>>
}

fun TaskHistoryApi.findCreationHistoryByTaskId(taskId: String): Single<Date> =
    findAllByTypeAndTaskId(taskId, "createCard")
        .flatMap {
            if (it.isEmpty())
                findAllByTypeAndTaskId(taskId, "convertToCardFromCheckItem").map { it.first().date }
            else
                Single.just(it.first().date)
        }