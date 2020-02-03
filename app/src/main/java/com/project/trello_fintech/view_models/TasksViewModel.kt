package com.project.trello_fintech.view_models

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.project.trello_fintech.api.*
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.reactive.LiveEvent
import com.project.trello_fintech.utils.reactive.LiveList
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * ViewModel для манипуляций над списком задач для каждой колонки отдельно
 * @property retrofitClient RetrofitClient
 * @property taskRetrofit TaskApi
 * @property taskAttachmentRetrofit TaskAttachmentApi
 * @property historyRetrofit TaskHistoryApi
 * @property tasks LinkedHashMap<Column, LiveList<Task>>
 * @property isLoading MutableLiveData<Boolean>
 * @property counterDown MutableLiveData<Int?>
 * @property onDataLoaded LiveEvent<Unit>
 * @property onError LiveEvent<Pair<String, Int?>>
 * @property onClick LiveEvent<String>
 */
class TasksViewModel(private val retrofitClient: RetrofitClient): CleanableViewModel() {

    companion object {
        @JvmStatic
        val currentTaskId = MutableLiveData<String>()
    }

    private val taskRetrofit by lazy { retrofitClient.create<TaskApi>(onError) }
    private val taskAttachmentRetrofit by lazy { retrofitClient.create<TaskAttachmentApi>(onError) }
    private val historyRetrofit by lazy { retrofitClient.create<TaskHistoryApi>(onError) }

    val tasks = linkedMapOf<Column, LiveList<Task>>()
    val isLoading = MutableLiveData<Boolean>()

    private val counterDown = MutableLiveData<Int?>(null).apply {
        observeForever { if (it == 0) onDataLoaded.emit() }
    }

    private val onDataLoaded = LiveEvent<Unit>()
    val onError = LiveEvent<Pair<String, Int?>>()

    fun load(column: Column) {
        tasks[column] = LiveList()
        val disposable = taskRetrofit.findAllByColumnId(column.id)
            .doOnSubscribe { isLoading.value = true }
            .doAfterSuccess {
                isLoading.value = false
                counterDown.value = counterDown.value?.dec()
            }
            .flattenAsObservable { it }
            .concatMapSingle { task ->
                val s1 = taskAttachmentRetrofit.findAllByTaskId(task.id)
                val s2 = historyRetrofit.findCreationHistoryByTaskId(task.id)

                Single.zip<List<Task.Attachment>, Date, Task>(s1, s2, BiFunction { attachments, creationDate ->
                    task.apply {
                        this.attachments = attachments
                        this.creationDate = creationDate
                    }
                })
            }
            .toList()
            .subscribe { taskList ->
                tasks.getValue(column).data = taskList
            }
        clearOnDestroy(disposable)
    }

    fun observeOnLoaded(columnsCount: Int, lifecycleOwner: LifecycleOwner, callback: () -> Unit) {
        counterDown.value = columnsCount
        onDataLoaded.observe(lifecycleOwner, Observer { callback() })
    }

    fun observe(column: Column, subscribe: (List<Task>) -> Unit) {
        val disposable = tasks.getValue(column)
            .observe()
            .subscribe(subscribe)
        clearOnDestroy(disposable)
    }

    fun add(column: Column, task: Task) {
        if (task.text.isBlank()) {
            val error = Pair("Текст новой задачи не должно быть пустым", null)
            onError.emit(error)
            return
        }
        val disposable = taskRetrofit.create(task, column.id).subscribe{ newTask ->
            tasks.getValue(column).add(newTask)
        }
        clearOnDestroy(disposable)
    }

    fun onItemDragStarted(column: Column, pos: Int) {
        currentTaskId.value = tasks.getValue(column)[pos].id
    }

    fun onItemDragEnded(fromColumn: Column, toColumn: Column, toRow: Int) {
        val currentTask = currentTaskId.value?.let {
            tasks.getValue(toColumn)[toRow].apply {
                taskRetrofit.updateColumn(it, toColumn.id, (toRow + 1).toString()).subscribe { updatedTask ->
                    trelloPos = updatedTask.trelloPos
                }
                currentTaskId.value = null
            }
        }
        Observable.just(
                tasks.getValue(fromColumn).data,
                tasks.getValue(toColumn).data.minus(currentTask).filterNotNull()
            )
            .flatMap {
                Observable.fromCallable { checkAndUpdatePositions(it) }
                    .subscribeOn(Schedulers.computation())
            }
            .subscribe()
    }

    private fun checkAndUpdatePositions(columnData: List<Task>) {
        columnData.forEachIndexed { index, task ->
            val trelloPosF = when (task.trelloPos) {
                "top" -> 0f
                "bottom" -> columnData.lastIndex.toFloat()
                else -> task.trelloPos.toFloat()
            }
            if (trelloPosF != index + 1f) {
                taskRetrofit.updatePos(task.id, (index + 1).toString()).subscribe { updatedTask ->
                    task.trelloPos = updatedTask.trelloPos
                }
            }
        }
    }

    private fun removeById(column: Column, id: String) {
        tasks.getValue(column).data.find{ it.id == id }?.let {
            taskRetrofit.delete(id).subscribe()
            tasks.getValue(column).remove(it)
        }
    }

    fun removeFromAllColumnsById() {
        currentTaskId.value?.let {
            for (column in tasks.keys) {
                removeById(column, it)
            }
            currentTaskId.value = null
        }
    }
}