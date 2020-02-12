package com.project.trello_fintech.services

import com.google.firebase.database.FirebaseDatabase
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskApi
import com.project.trello_fintech.models.Board
import javax.inject.Inject


/**
 * Сервис взаимодействия с Firebase Relatime Database
 * @property database FirebaseDatabase
 * @property taskApi TaskApi
 */
class FirebaseService @Inject constructor(retrofitClient: RetrofitClient) {

    private val database = FirebaseDatabase.getInstance()
    private val taskApi by lazy { retrofitClient.create<TaskApi>() }

    fun registerBoards(boards: List<Board>) {
        val boardsRef = database.reference.child("boards")
        boards.forEach {
            taskApi.findAllByBoardId(it.id).subscribe { tasks ->
                val tasksData = tasks.associateBy({ task -> task.id }, { task -> task.text })
                boardsRef.child(it.id).child("tasks").updateChildren(tasksData)
            }
        }
    }
}