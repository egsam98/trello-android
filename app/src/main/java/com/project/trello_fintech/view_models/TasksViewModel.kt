package com.project.trello_fintech.view_models

import androidx.lifecycle.*
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskApi
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.reactive.LiveEvent
import com.project.trello_fintech.utils.reactive.LiveList
import io.reactivex.rxkotlin.cast


/**
 * ViewModel для манипуляций над списком задач для каждой колонки отдельно
 * @property retrofitClient RetrofitClient
 * @property retrofit TaskApi
 * @property tasks LinkedHashMap<Column, LiveList<Task>>
 * @property isLoading MutableLiveData<Boolean>
 * @property onError LiveEvent<Pair<String, Int?>>
 */
class TasksViewModel(private val retrofitClient: RetrofitClient): CleanableViewModel() {

    companion object {
        @JvmStatic
        val currentTaskId = MutableLiveData<String>()
    }

    private val retrofit by lazy { retrofitClient.create<TaskApi>(onError) }
    private val tasks = linkedMapOf<Column, LiveList<Task>>()
    var isLoading = MutableLiveData<Boolean>()
        private set

    val onError = LiveEvent<Pair<String, Int?>>()

    fun load(column: Column) {
        tasks[column] = LiveList()
        val disposable = retrofit.findAllByColumnId(column.id)
            .doOnSubscribe { isLoading.value = true }
            .doAfterSuccess { isLoading.value = false }
            .cast<MutableList<Task>>()
            .subscribe { taskList ->
                tasks.getValue(column).data = taskList
            }
        clearOnDestroy(disposable)
    }

    fun observe(column: Column, subscribe: (List<Task>) -> Unit) {
        val disposable = tasks.getValue(column)
            .observe()
            .subscribe(subscribe)
        clearOnDestroy(disposable)
    }

    fun add(column: Column, task: Task) {
        val disposable = retrofit.create(task, column.id).subscribe{ newTask ->
            tasks.getValue(column).add(newTask)
        }
        clearOnDestroy(disposable)
    }

    fun onItemDragStarted(column: Column, pos: Int) {
        currentTaskId.value = tasks.getValue(column)[pos].id
    }

    fun onItemDragEnded(column: Column, newPos: Int) {
        tasks.getValue(column).data.find { it.id == currentTaskId.value }?.let {
            retrofit.updateColumn(it.id, column.id, newPos.toString()).subscribe()
        }
        currentTaskId.value = null
    }

    private fun removeById(column: Column, id: String) {
        tasks.getValue(column).data.find{ it.id == id }?.let {
            retrofit.delete(id).subscribe()
            tasks.getValue(column).remove(it)
        }
    }

    fun removeFromAllColumnsById(id: String) {
        for (column in tasks.keys) {
            removeById(column, id)
        }
    }
}