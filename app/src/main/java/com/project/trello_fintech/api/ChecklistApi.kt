package com.project.trello_fintech.api

import com.project.trello_fintech.models.Checklist
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ChecklistApi {
    @GET("cards/{id}/checklists")
    fun findAllByTaskId(@Path("id") id: String): Single<List<Checklist>>
}