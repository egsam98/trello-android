package com.project.trello_fintech.view_models.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.di.scopes.MainActivityScope
import com.project.trello_fintech.services.AuthenticationService
import com.project.trello_fintech.services.FirebaseService
import com.project.trello_fintech.utils.TrelloUtil
import com.project.trello_fintech.view_models.BoardsViewModel
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.TasksViewModel
import com.project.trello_fintech.view_models.UsersViewModel
import javax.inject.Inject


/**
 * Фабрика ViewModel'ей
 * @property cxt Context
 * @property retrofitClient RetrofitClient
 * @property firebaseService FirebaseService
 */
@MainActivityScope
@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(
    private val cxt: Context,
    private val retrofitClient: RetrofitClient,
    private val trelloUtil: TrelloUtil,
    private val authService: AuthenticationService,
    private val firebaseService: FirebaseService): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BoardsViewModel::class.java) -> BoardsViewModel(firebaseService, retrofitClient)
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> TasksViewModel(firebaseService, retrofitClient)
            modelClass.isAssignableFrom(UsersViewModel::class.java) -> UsersViewModel(retrofitClient)
            modelClass.isAssignableFrom(TaskDetailViewModel::class.java) ->
                TaskDetailViewModel(cxt, retrofitClient, trelloUtil, firebaseService, authService)
            else -> throw IllegalArgumentException("Unregistered ViewModel $modelClass in ViewModelFactory")
        } as T
    }
}