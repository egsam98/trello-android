package com.project.trello_fintech.api

import com.project.trello_fintech.models.Task
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*


interface TaskAttachmentApi {
    @GET("cards/{id}/attachments")
    fun findAllByTaskId(@Path("id") id: String): Single<List<Task.Attachment>>

    @Multipart
    @POST("cards/{id}/attachments")
    fun create(@Path("id") id: String,
               @Query("mimeType") mimeType: String,
               @Part filePart: MultipartBody.Part): Single<Task.Attachment>

    @DELETE("cards/{cardId}/attachments/{attachmentId}")
    fun delete(@Path("cardId") cardId: String, @Path("attachmentId") attachmentId: String): Completable
}