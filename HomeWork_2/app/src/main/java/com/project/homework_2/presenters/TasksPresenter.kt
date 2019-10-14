package com.project.homework_2.presenters

import com.project.homework_2.models.Task

/**
 * Презентер для манипуляций над списком задач для каждой колонки по отдельно
 * @see com.project.homework_2.fragments.TasksFragment BoardView
 * @property tasks MutableList<Task>
 * @property iView IView?
 */
class TasksPresenter(val tasks: MutableList<Task>, var iView: IView? = null): IListPresenter<Task> {

    private var currentTaskDragging: Task? = null

    override fun add(task: Task) {
        tasks.add(task)
        iView?.onTasksChange()
    }

    override fun removeAt(pos: Int) {
        if (pos >= 0 && pos < tasks.size) {
            tasks.removeAt(pos)
        }
        iView?.onTasksChange()
    }

    fun onItemDragStarted(pos: Int) {
        currentTaskDragging = tasks[pos]
    }

    fun onItemDragEnded() = currentTaskDragging?.let {
        tasks.remove(it)
        iView?.onTasksChange()
        currentTaskDragging = null
    }

    interface IView {
        fun getPresenter(): TasksPresenter
        fun onTasksChange()
    }
}