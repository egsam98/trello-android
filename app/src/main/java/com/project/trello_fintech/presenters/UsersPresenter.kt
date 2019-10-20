package com.project.trello_fintech.presenters

import android.content.Context
import android.graphics.BitmapFactory
import com.project.homework_2.R
import com.project.trello_fintech.models.User


/**
 * Презентер для манипуляций над списком пользователей
 * @property users Array<User>
 */
object UsersPresenter {
    var users = arrayOf<User>()
    private set

    fun createMocks(context: Context) {
        users = arrayOf(
            User(
                "Clint Eastwood",
                BitmapFactory.decodeResource(context.resources, R.drawable.clint_eastwood)
            ),
            User("Unknown")
        )
    }
}