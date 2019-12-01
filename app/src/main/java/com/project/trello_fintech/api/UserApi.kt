package com.project.trello_fintech.api

import com.project.trello_fintech.models.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("boards/{id}/members")
    fun findAllByBoardId(@Path("id") id: String): Single<List<User>>

    @GET("cards/{id}/members")
    fun findAllByTaskId(@Path("id") id: String): Single<List<User>>
}