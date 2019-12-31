package com.project.trello_fintech.api

import com.project.trello_fintech.models.Task
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*


interface TaskHistoryApi {

    @GET("cards/{id}/actions")
    fun findAllByTaskId(@Path("id") id: String,
                        @Query("filter") filter: String = "all"): Single<List<Task.History>>

    @GET("cards/{id}/actions")
    fun findHistoryByTypeAndTaskId(@Path("id") taskId: String,
                                   @Query("filter") type: String): Single<List<Task.History>>
}

fun TaskHistoryApi.findCreationHistoryByTaskId(taskId: String): Single<Date> =
    findHistoryByTypeAndTaskId(taskId, "createCard").map { it[0].date }