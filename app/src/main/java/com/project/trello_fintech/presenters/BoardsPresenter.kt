package com.project.trello_fintech.presenters

import com.project.trello_fintech.api.BoardApi
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.CategoryApi
import com.project.trello_fintech.api.ColumnApi
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.IListItem
import com.project.trello_fintech.models.NothingListItem
import com.project.trello_fintech.utils.LiveList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.cast


/**
 * Возможные фоновые цвета доски
 */
private val COLORS = arrayOf("blue", "orange", "green", "red", "purple", "pink", "lime", "sky", "grey")

/**
 * Презентер для манипуляций над списком досок
 * @property boardsView IAdapter
 * @property boards LiveList<Board>
 * @see LiveList
 * @property listItems List<IListItem> список досок и категорий (доски сгруппированы по категориям)
 * (перемещение досок или удаление)
 */
object BoardsPresenter {

    private val boardRetrofit = RetrofitClient.create<BoardApi>()
    private val categoryRetrofit = RetrofitClient.create<CategoryApi>()

    private var boards = LiveList<Board>()
    var listItems = listOf<IListItem>()
        private set

    var boardsView: IView? = null

    fun observe(): Observable<Pair<List<IListItem>, List<IListItem>>> = boards
        .observe()
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

    private fun load() {
        boardRetrofit.findAll()
            .cast<MutableList<Board>>()
            .subscribe {
                boards.data = it
            }
    }

    fun init(mvpView: IView) {
        boardsView = mvpView
        load()
    }

    fun add(board: Board) {
        if (board.title.isBlank()) {
            boardsView?.showError("Название новой доски не должно быть пустым")
            return
        }
        board.category?.let { category ->
            boardRetrofit.create(board, category.id, COLORS.random())
                .subscribe {
                    it.category = category
                    boards add it
                    onClick(it)
                }
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
        val retrofit = RetrofitClient.create<ColumnApi>()
        retrofit.findAllByBoardId(board.id)
            .subscribe {
                board.columns = it
                boardsView?.showTasks(board)
            }
    }

    fun getAllCategories() = categoryRetrofit.findAllAvailable()
        .map { it.toMutableList().apply { add(0, Board.Category.default()) } }
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * Интерфейс, который Activity/Fragment должен реализовать для взаимодействия с презентером
     */
    interface IView {
        /**
         * Отобразить список задач по выбранной на UI доске
         * @param board Board
         */
        fun showTasks(board: Board)

        /**
         * Сообщение об ошибке
         * @param message String
         */
        fun showError(message: String, code: Int? = null)

        fun openWebViewForToken()
    }
}