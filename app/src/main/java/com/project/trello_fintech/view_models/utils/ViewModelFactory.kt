package com.project.trello_fintech.view_models.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.view_models.BoardsViewModel
import com.project.trello_fintech.view_models.TasksViewModel
import com.project.trello_fintech.view_models.UsersViewModel


/**
 * Фабрика создания ViewModel'ей
 * @property retrofitClient RetrofitClient
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val retrofitClient: RetrofitClient): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BoardsViewModel::class.java) -> BoardsViewModel(retrofitClient)
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> TasksViewModel(retrofitClient)
            modelClass.isAssignableFrom(UsersViewModel::class.java) -> UsersViewModel()
            else -> throw IllegalArgumentException("Unregistered ViewModel $modelClass in ViewModelFactory")
        } as T
    }
}