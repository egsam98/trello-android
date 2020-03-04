package com.project.trello_fintech.api

import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.Task
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface TaskApi {

    @GET("cards/{id}")
    fun findById(@Path("id") id: String): Single<Task>

    @GET("list/{id}/cards")
    fun findAllByColumnId(@Path("id") id: String): Single<List<Task>>

    @GET("boards/{id}/cards")
    fun findAllByBoardId(@Path("id") id: String): Single<List<Task>>

    @GET("cards/{id}/board")
    fun findBoard(@Path("id") taskId: String): Single<Board>

    @POST("cards/")
    fun create(@Body task: Task, @Query("idList") idList: String): Single<Task>

    @PUT("cards/{id}")
    fun updateColumn(@Path("id") id: String,
                     @Query("idList") idList: String,
                     @Query("pos") pos: String = "bottom"): Single<Task>

    @PUT("cards/{id}")
    fun updatePos(@Path("id") id: String, @Query("pos") pos: String): Single<Task>

    @PUT("cards/{id}")
    fun updateDescription(@Path("id") id: String, @Query("desc") description: String): Single<Task>

    @DELETE("cards/{id}")
    fun delete(@Path("id") id: String): Completable
}