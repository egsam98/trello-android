package com.project.trello_fintech.api

import com.project.trello_fintech.models.Board
import io.reactivex.Single
import retrofit2.http.GET


interface CategoryApi {
    @GET("members/me/organizations")
    fun findAllAvailable(): Single<List<Board.Category>>
}