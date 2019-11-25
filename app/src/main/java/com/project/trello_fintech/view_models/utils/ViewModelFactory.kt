package com.project.trello_fintech.view_models.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.view_models.BoardsViewModel
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.TasksViewModel
import com.project.trello_fintech.view_models.UsersViewModel


/**
 * Фабрика создания ViewModel'ей
 * @property cxt Context
 * @property retrofitClient RetrofitClient
 * @constructor
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val cxt: Context, private val retrofitClient: RetrofitClient): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BoardsViewModel::class.java) -> BoardsViewModel(retrofitClient)
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> TasksViewModel(retrofitClient)
            modelClass.isAssignableFrom(UsersViewModel::class.java) -> UsersViewModel()
            modelClass.isAssignableFrom(TaskDetailViewModel::class.java) -> TaskDetailViewModel(cxt, retrofitClient)
            else -> throw IllegalArgumentException("Unregistered ViewModel $modelClass in ViewModelFactory")
        } as T
    }
}