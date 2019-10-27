package com.project.trello_fintech.api

import com.project.trello_fintech.models.Column
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ColumnApi {
    @GET("boards/{id}/lists")
    fun findAllByBoardId(@Path("id") id: String): Observable<List<Column>>
}