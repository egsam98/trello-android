package com.project.trello_fintech.view_models

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.project.trello_fintech.api.BoardApi
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.CategoryApi
import com.project.trello_fintech.api.ColumnApi
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.IListItem
import com.project.trello_fintech.models.NothingListItem
import com.project.trello_fintech.utils.reactive.LiveEvent
import com.project.trello_fintech.utils.reactive.LiveList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.cast


/**
 * Возможные фоновые цвета доски
 */
private val COLORS = arrayOf("blue", "orange", "green", "red", "purple", "pink", "lime", "sky", "grey")

/**
 * ViewModel для манипуляций над списком досок
 * @property disposables CompositeDisposable
 * @property boardRetrofit BoardApi
 * @property categoryRetrofit CategoryApi
 * @property retrofit ColumnApi
 * @property boards LiveList<Board>
 * @property listItems List<IListItem> список досок и категорий (доски сгруппированы по категориям)
 * (перемещение досок или удаление)
 * @property isLoading MutableLiveData<Boolean>
 * @property onClick LiveEvent<Board>
 * @property onError LiveEvent<Pair<String, Int?>>
 */
class BoardsViewModel: ViewModel() {

    private val disposables = CompositeDisposable()
    private val boardRetrofit by lazy { RetrofitClient.create<BoardApi>() }
    private val categoryRetrofit by lazy { RetrofitClient.create<CategoryApi>() }
    private val retrofit by lazy { RetrofitClient.create<ColumnApi>() }
    private val boards = LiveList<Board>()
    var listItems = listOf<IListItem>()
        private set
    val isLoading = MutableLiveData<Boolean>()
    val onClick = LiveEvent<Board>()
    val onError = LiveEvent<Pair<String, Int?>>()

    fun observe(owner: LifecycleOwner, observer: Observer<in Pair<List<IListItem>, List<IListItem>>>) {
        val publisher = boards.observe()
            .map { boards ->
                for (board in boards) {
                    if (board.category == null)
                        board.category = Board.Category.default()
                }
                val before = listItems.toList()
                listItems =
                    if (boards.isNotEmpty())
                        boards
                            .groupBy { it.category!! }
                            .toSortedMap()
                            .flatMap { (key, boards) ->
                                mutableListOf<IListItem>(key).apply { addAll(boards) }
                            }
                    else
                        listOf(NothingListItem)
                Pair(before, listItems)
            }

        LiveDataReactiveStreams
            .fromPublisher(publisher)
            .observe(owner, observer)
    }

    fun load(swipeRefreshLayout: SwipeRefreshLayout? = null) {
        val disposable = boardRetrofit.findAll()
            .doOnSubscribe { isLoading.value = true }
            .doAfterNext {
                isLoading.value = false
                swipeRefreshLayout?.isRefreshing = false
            }
            .cast<MutableList<Board>>()
            .subscribe {
                boards.data = it
            }
        disposables.add(disposable)
    }

    fun add(board: Board) {
        if (board.title.isBlank()) {
            val error = Pair("Название новой доски не должно быть пустым", null)
            onError.emit(error)
            return
        }
        board.category?.let { category ->
            val disposable = boardRetrofit.create(board, category.id, COLORS.random())
                .subscribe {
                    it.category = category
                    boards add it
                    onClick(it)
                }
            disposables.add(disposable)
        }
    }

    fun remove(board: Board) {
        boardRetrofit.delete(board.id).subscribe()
        boards remove board
    }

    fun move(source: Board, target: Board) {
        boards.data.find(source::equals)?.let {
            val targetCategory = target.category
            if (targetCategory != null && it.category != targetCategory) {
                boardRetrofit.update(it, idOrg = targetCategory.id).subscribe()
                it.category = targetCategory
            }
            boards.move(source, target)
        }
    }

    fun onClick(board: Board) {
        val disposable = retrofit.findAllByBoardId(board.id).subscribe { columns ->
            board.columns = columns
            onClick.emit(board)
        }
        disposables.add(disposable)
    }

    fun getAllCategories() = categoryRetrofit.findAllAvailable()
        .map { it.toMutableList().apply { add(0, Board.Category.default()) } }
        .observeOn(AndroidSchedulers.mainThread())

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}


fun <T>ObservableArrayList<T>.move(source: T, target: T) {
    val targetIndex = this.indexOf(target)
    this.remove(source)
    this.add(targetIndex, source)
}