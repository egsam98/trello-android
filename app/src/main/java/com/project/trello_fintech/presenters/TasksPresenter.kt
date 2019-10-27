package com.project.trello_fintech.presenters

import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskApi
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.LiveList
import io.reactivex.rxkotlin.cast
import java.io.Serializable


/**
 * Презентер для манипуляций над списком задач для каждой колонки отдельно
 * @see com.project.trello_fintech.fragments.TasksFragment BoardView
 * @property tasks MutableList<Task>
 * @property adapter IAdapter?
 */
class TasksPresenter(column: Column) {

    companion object {
        var currentTaskId: String? = null
            private set
    }

    private val columnId = column.id

    val tasks = LiveList<Task>()

    private val retrofit = RetrofitClient.create<TaskApi>()

    var adapter: IAdapter? = null

    init {
        retrofit.findAllByColumnId(column.id)
            .cast<MutableList<Task>>()
            .subscribe {
                tasks.data = it
                adapter?.onTasksChange()
            }
    }

    fun observe() = tasks.observe()

    fun add(task: Task) {
        retrofit.create(task, columnId).subscribe{
            tasks.add(it)
            adapter?.onTasksChange()
        }
    }

    fun onItemDragStarted(pos: Int) {
        currentTaskId = tasks[pos].id
    }

    fun onItemDragEnded(newPos: Int) {
        tasks.data.find { it.id == currentTaskId }?.let {
            retrofit.updateColumn(it.id, columnId, newPos.toString()).subscribe()
        }
        currentTaskId = null
    }

    fun removeById(id: String) {
        tasks.data.find{ it.id == id }?.let {
            retrofit.delete(id).subscribe()
            tasks.remove(it)
            adapter?.onTasksChange()
        }
    }

    interface IAdapter {
        fun getPresenter(): TasksPresenter
        fun onTasksChange()
    }
}