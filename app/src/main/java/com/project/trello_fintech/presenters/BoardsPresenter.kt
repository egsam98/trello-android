package com.project.trello_fintech.presenters

import android.content.Context
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.IListItem
import com.project.trello_fintech.models.NothingListItem
import com.project.trello_fintech.utils.LiveList
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.lang.Exception


/**
 * Презентер для манипуляций над списком досок
 * @property boardsView IAdapter
 * @property boards LiveList<Board>
 * @see LiveList
 * @property listItems List<IListItem> список досок и категорий (доски сгруппированы по категориям)
 * (перемещение досок или удаление)
 */
object BoardsPresenter {

    /**
     * Файл с данным названием хранится в InternalStorage
     */
    private const val BOARDS_FILENAME = "boards.bin"
    private var boards = LiveList<Board>()
    private var listItems = listOf<IListItem>()

    var boardsView: IView? = null

    fun observe(): Observable<Pair<List<IListItem>, List<IListItem>>> = boards
        .observe()
        .subscribeOn(Schedulers.computation())
        .map { boards ->
            val before = listItems.toList()
            listItems =
                if (boards.isNotEmpty())
                    boards
                        .groupBy { it.category }
                        .toSortedMap()
                        .flatMap { (key, boards) ->
                            mutableListOf<IListItem>(key).apply { addAll(boards) }
                        }
                else
                    listOf(NothingListItem)
            Pair(before, listItems)
        }

    private fun load(context: Context) {
        try {
            val fis = context.openFileInput(BOARDS_FILENAME)
            ObjectInputStream(fis).use {
                boards.data = it.readObject() as MutableList<Board>
            }
        } catch (e: Exception) {
            // Иначе пустой список
        }
    }

    fun init(context: Context) {
        load(context)
        if (context is IView)
            boardsView = context
    }

    fun save(context: Context) {
        ObjectOutputStream(context.openFileOutput(BOARDS_FILENAME, Context.MODE_PRIVATE)).use {
            it.writeObject(boards.data)
        }
    }

    fun add(board: Board) {
        if (board.title.isBlank()) {
            boardsView?.showError("Название новой доски не должно быть пустым")
            return
        }
        with(board) {
            boards add board
            boardsView?.showTasks(this)
        }
    }

    fun remove(board: Board) {
        boards remove board
    }

    fun move(source: Board, target: Board) {
        boards.data.find(source::equals)?.let {
            it.category = target.category
            boards.move(source, target)
        }
    }

    fun onClick(board: Board) {
        boardsView?.showTasks(board)
    }

    fun getAllCategories() = arrayOf(
        Board.Category("Personal boards"),
        Board.Category("Work boards"),
        Board.Category("Other")
    )

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
        fun showError(message: String)
    }
}