package com.project.trello_fintech.view_models

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.UserApi
import com.project.trello_fintech.models.User
import com.project.trello_fintech.utils.reactive.LiveEvent
import io.reactivex.Single
import io.reactivex.functions.BiFunction


/**
 * ViewModel для манипуляций над списком пользователей
 * @property users MutableLiveData<Array<User>>
 */
class UsersViewModel(private val retrofitClient: RetrofitClient): CleanableViewModel() {
    private val users = MutableLiveData<Array<User>>()

    private val userRetrofit by lazy {
        retrofitClient.create<UserApi>(onError)
    }

    val onError = LiveEvent<Pair<String, Int?>>()

    fun observeBoardAndTaskUsers(boardId: String, taskId: String, subscribe: (Pair<List<User>, List<User>>) -> Unit) {
        val boardsSingle = userRetrofit.findAllByBoardId(boardId)
        val tasksSingle = userRetrofit.findAllByTaskId(taskId)

        val disposable = Single
            .zip(boardsSingle, tasksSingle,
                BiFunction {
                    boards: List<User>, tasks: List<User> -> Pair(boards, tasks)
                })
            .subscribe { pair ->
                subscribe(pair)
            }
        clearOnDestroy(disposable)
    }

    fun createMocks(context: Context) {
        users.value = arrayOf(
//            User(
//                "Clint Eastwood",
//                BitmapFactory.decodeResource(context.resources, R.drawable.clint_eastwood)
//            ),
            User("Unknown")
        )
    }
}