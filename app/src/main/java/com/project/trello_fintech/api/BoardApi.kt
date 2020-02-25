package com.project.trello_fintech.api

import com.project.trello_fintech.models.Board
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface BoardApi {
    @GET("members/me/boards?organization=true&organization_fields=displayName")
    fun findAll(): Single<List<Board>>

    @GET("boards/{id}")
    fun findById(@Path("id") id: String): Single<Board>

    @POST("boards/?defaultLists=true")
    fun create(@Body board: Board,
               @Query("idOrganization") id: String,
               @Query("prefs_background") background: String): Single<Board>

    @PUT("boards/{id}")
    fun update(@Body board: Board,
               @Path("id") id: String = board.id,
               @Query("idOrganization") idOrg: String): Single<Board>

    @DELETE("boards/{id}")
    fun delete(@Path("id") id: String): Completable
}