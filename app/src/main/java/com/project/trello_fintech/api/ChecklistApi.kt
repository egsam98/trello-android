package com.project.trello_fintech.api

import com.project.trello_fintech.models.Checklist
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*


interface ChecklistApi {
    @GET("cards/{id}/checklists")
    fun findAllByTaskId(@Path("id") id: String): Single<List<Checklist>>

    @POST("checklists/")
    fun create(@Query("name") title: String, @Query("idCard") taskId: String): Single<Checklist>

    @PUT("checklists/{id}/name")
    fun updateTitle(@Path("id") id: String, @Query("value") title: String): Completable

    @DELETE("checklists/{id}")
    fun delete(@Path("id") id: String): Completable
}