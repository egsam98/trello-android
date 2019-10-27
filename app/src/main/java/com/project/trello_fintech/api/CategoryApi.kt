package com.project.trello_fintech.api

import com.project.trello_fintech.models.Board
import io.reactivex.Observable
import retrofit2.http.GET


interface CategoryApi {
    @GET("members/me/organizations")
    fun findAllAvailable(): Observable<List<Board.Category>>
}