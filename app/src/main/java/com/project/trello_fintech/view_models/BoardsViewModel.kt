package com.project.trello_fintech.view_models

import androidx.lifecycle.*
import com.project.trello_fintech.Application
import com.project.trello_fintech.api.BoardApi
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.CategoryApi
import com.project.trello_fintech.api.ColumnApi
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.IListItem
import com.project.trello_fintech.models.NothingListItem
import com.project.trello_fintech.services.FirebaseService
import com.project.trello_fintech.utils.reactive.LiveEvent
import com.project.trello_fintech.utils.reactive.LiveList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.cast
import javax.inject.Inject


/**
 * Возможные фоновые цвета доски
 */
private val COLORS = arrayOf("blue", "orange", "green", "red", "purple", "pink", "lime", "sky", "grey")

/**
 * ViewModel для манипуляций над списком досок
 * @property boardRetrofit BoardApi
 * @property categoryRetrofit CategoryApi
 * @property retrofit ColumnApi
 * @property boards LiveList<Board>
 * @property isLoading MutableLiveData<Boolean>
 * @property onClick LiveEvent<Board>
 * @property onError LiveEvent<Pair<String, Int?>>
 */
class BoardsViewModel(private val retrofitClient: RetrofitClient): CleanableViewModel() {

    @Inject
    lateinit var firebaseService: FirebaseService

    private val boardRetrofit by lazy { retrofitClient.create<BoardApi>(onError) }
    private val categoryRetrofit by lazy { retrofitClient.create<CategoryApi>(onError) }
    private val retrofit by lazy { retrofitClient.create<ColumnApi>(onError) }
    private val boards = LiveList<Board>()
    val isLoading = MutableLiveData<Boolean>()
    val onClick = LiveEvent<Board>()
    val onError = LiveEvent<Pair<String, Int?>>()

    init { Application.component.inject(this) }

    fun observe(subscribe: (List<IListItem>) -> Unit) {
        val disposable = boards.observe()
            .map { boards ->
                for (board in boards) {
                    if (board.category == null)
                        board.category = Board.Category.default()
                }
                if (boards.isNotEmpty())
                    boards
                        .groupBy { it.category!! }
                        .toSortedMap()
                        .flatMap { (key, boards) ->
                            mutableListOf<IListItem>(key).apply { addAll(boards) }
                        }
                    else
                        listOf(NothingListItem)
            }
            .subscribe(subscribe)

        clearOnDestroy(disposable)
    }

    fun load() {
        val disposable = boardRetrofit.findAll()
            .doOnSubscribe { isLoading.value = true }
            .doOnSuccess { isLoading.value = false }
            .cast<MutableList<Board>>()
            .subscribe { boardList ->
                boards.data = boardList
                boardList.forEach {
                    firebaseService.registerBoard(it)
                }
            }
        clearOnDestroy(disposable)
    }

    fun add(board: Board) {
        if (board.title.isBlank()) {
            val error = Pair("Название новой доски не должно быть пустым", null)
            onError.emit(error)
            return
        }
        board.category?.let { category ->
            val disposable = boardRetrofit.create(board, category.id, COLORS.random())
                .subscribe { board ->
                    board.category = category
                    firebaseService.registerBoard(board)
                    boards add board
                    onClick(board)
                }
            clearOnDestroy(disposable)
        }
    }

    fun remove(board: Board) {
        val disposable = boardRetrofit.delete(board.id).subscribe {
            firebaseService.deleteBoard(board)
        }
        boards remove board
        clearOnDestroy(disposable)
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
        clearOnDestroy(disposable)
    }

    fun getAllCategories() = categoryRetrofit.findAllAvailable()
        .map { it.toMutableList().apply { add(0, Board.Category.default()) } }
        .observeOn(AndroidSchedulers.mainThread())
}
