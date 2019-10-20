package com.project.trello_fintech.presenters

import com.project.trello_fintech.models.Task

/**
 * Презентер для манипуляций над списком задач для каждой колонки отдельно
 * @see com.project.homework_2.fragments.TasksFragment BoardView
 * @property tasks MutableList<Task>
 * @property adapter IAdapter?
 */
class TasksPresenter(val tasks: MutableList<Task>) {

    companion object {
        var focusedColumnInd: Int = -1
        private set

        var currentTaskId: Long? = null
        private set

        fun onItemDragEnded() {
            currentTaskId = null
            focusedColumnInd = -1
        }
    }

    var adapter: IAdapter? = null

    fun add(task: Task) {
        tasks.add(task)
        adapter?.onTasksChange()
    }

    fun onItemDragStarted(columnInd: Int, pos: Int) {
        currentTaskId = tasks[pos].id
        focusedColumnInd = columnInd
    }

    fun removeById(id: Long) {
        tasks.removeAll { it.id == id }
        adapter?.onTasksChange()
    }

    interface IAdapter{
        fun getPresenter(): TasksPresenter
        fun onTasksChange()
    }
}