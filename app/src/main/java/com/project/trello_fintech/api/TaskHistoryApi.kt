package com.project.trello_fintech.api

import com.project.trello_fintech.models.Task
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TaskHistoryApi {

    @GET("cards/{id}/actions")
    fun findAllByTaskId(@Path("id") id: String,
                        @Query("filter") filter: String = "all"): Single<List<Task.History>>
}