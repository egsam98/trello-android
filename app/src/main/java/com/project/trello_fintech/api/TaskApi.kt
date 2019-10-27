package com.project.trello_fintech.api

import com.project.trello_fintech.models.Task
import io.reactivex.Observable
import retrofit2.http.*

interface TaskApi {
    @GET("list/{id}/cards")
    fun findAllByColumnId(@Path("id") id: String): Observable<List<Task>>

    @POST("cards/")
    fun create(@Body task: Task, @Query("idList") idList: String): Observable<Task>

    @PUT("cards/{id}")
    fun updateColumn(@Path("id") id: String,
                     @Query("idList") idList: String,
                     @Query("pos") pos: String = "bottom"): Observable<Task>

    @DELETE("cards/{id}")
    fun delete(@Path("id") id: String): Observable<Unit>
}