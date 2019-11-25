package com.project.trello_fintech.view_models

import androidx.lifecycle.*
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskApi
import com.project.trello_fintech.api.TaskAttachmentApi
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.reactive.LiveEvent
import com.project.trello_fintech.utils.reactive.LiveList


/**
 * ViewModel для манипуляций над списком задач для каждой колонки отдельно
 * @property retrofitClient RetrofitClient
 * @property taskRetrofit TaskApi
 * @property taskAttachmentRetrofit TaskAttachmentApi
 * @property tasks LinkedHashMap<Column, LiveList<Task>>
 * @property isLoading MutableLiveData<Boolean>
 * @property onError LiveEvent<Pair<String, Int?>>
 * @property onClick LiveEvent<Task>
 */
class TasksViewModel(private val retrofitClient: RetrofitClient): CleanableViewModel() {

    companion object {
        @JvmStatic
        val currentTaskId = MutableLiveData<String>()
    }

    private val taskRetrofit by lazy { retrofitClient.create<TaskApi>(onError) }
    private val taskAttachmentRetrofit by lazy { retrofitClient.create<TaskAttachmentApi>(onError) }
    private val tasks = linkedMapOf<Column, LiveList<Task>>()
    var isLoading = MutableLiveData<Boolean>()
        private set

    val onError = LiveEvent<Pair<String, Int?>>()
    val onClick = LiveEvent<Task>()

    fun load(column: Column) {
        tasks[column] = LiveList()
        val disposable = taskRetrofit.findAllByColumnId(column.id)
            .doOnSubscribe { isLoading.value = true }
            .doAfterSuccess { isLoading.value = false }
            .flattenAsObservable{ it }
            .concatMap { task ->
                taskAttachmentRetrofit.findAllByTaskId(task.id)
                    .map { task.apply { attachments = it }}
                    .toObservable()
            }
            .toList()
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
        val disposable = taskRetrofit.create(task, column.id).subscribe{ newTask ->
            tasks.getValue(column).add(newTask)
        }
        clearOnDestroy(disposable)
    }

    fun onItemDragStarted(column: Column, pos: Int) {
        currentTaskId.value = tasks.getValue(column)[pos].id
    }

    fun onItemDragEnded(column: Column, newPos: Int) {
        tasks.getValue(column).data.find { it.id == currentTaskId.value }?.let {
            taskRetrofit.updateColumn(it.id, column.id, newPos.toString()).subscribe()
        }
        currentTaskId.value = null
    }

    private fun removeById(column: Column, id: String) {
        tasks.getValue(column).data.find{ it.id == id }?.let {
            taskRetrofit.delete(id).subscribe()
            tasks.getValue(column).remove(it)
        }
    }

    fun removeFromAllColumnsById(id: String) {
        for (column in tasks.keys) {
            removeById(column, id)
        }
    }
}