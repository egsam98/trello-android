package com.project.trello_fintech.api

import com.project.trello_fintech.models.Board
import io.reactivex.Observable
import retrofit2.http.*

interface BoardApi {
    @GET("members/me/boards?organization=true&organization_fields=displayName")
    fun findAll(): Observable<List<Board>>

    @POST("boards/?defaultLists=true")
    fun create(@Body board: Board,
               @Query("idOrganization") id: String,
               @Query("prefs_background") background: String): Observable<Board>

    @PUT("boards/{id}")
    fun update(@Body board: Board,
               @Path("id") id: String = board.id,
               @Query("idOrganization") idOrg: String): Observable<Board>

    @DELETE("boards/{id}")
    fun delete(@Path("id") id: String): Observable<Unit>
}