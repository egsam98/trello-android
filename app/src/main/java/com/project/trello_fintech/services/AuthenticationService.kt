package com.project.trello_fintech.services

import android.content.Context
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.UserApi
import com.project.trello_fintech.models.User
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Сервис взаимодействия с текущим авторизованным пользователем
 * @property userApi UserApi
 * @property user User
 */
@Singleton
class AuthenticationService @Inject constructor(
    cxt: Context,
    retrofitClient: RetrofitClient) {

    private val userApi by lazy {
        retrofitClient.create<UserApi>(scheduler = Schedulers.io())
    }

    val user: User
        get()  = userApi.findUser("me").blockingGet()
}