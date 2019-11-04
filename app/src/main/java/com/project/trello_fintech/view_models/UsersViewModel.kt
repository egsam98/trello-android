package com.project.trello_fintech.view_models

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.trello_fintech.R
import com.project.trello_fintech.models.User


/**
 * ViewModel для манипуляций над списком пользователей
 * @property users MutableLiveData<Array<User>>
 */
class UsersViewModel: ViewModel() {
    val users = MutableLiveData<Array<User>>()

    fun createMocks(context: Context) {
        users.value = arrayOf(
            User(
                "Clint Eastwood",
                BitmapFactory.decodeResource(context.resources, R.drawable.clint_eastwood)
            ),
            User("Unknown")
        )
    }
}