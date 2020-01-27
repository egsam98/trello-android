package com.project.trello_fintech.api

import com.project.trello_fintech.models.Checklist
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ChecklistApi {
    @GET("cards/{id}/checklists")
    fun findAllByTaskId(@Path("id") id: String): Single<List<Checklist>>

    @PUT("checklists/{id}/name")
    fun updateTitle(@Path("id") id: String, @Query("value") title: String): Completable
}