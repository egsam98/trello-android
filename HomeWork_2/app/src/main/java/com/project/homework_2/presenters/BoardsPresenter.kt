package com.project.homework_2.presenters

import android.content.Context
import android.os.Handler
import com.project.homework_2.models.Board
import java.io.*
import java.lang.Exception


/**
 * Презентер для манипуляций над списком досок
 * @property boardsView IView
 * @property boards ArrayList<Board>
 */
object BoardsPresenter: IListPresenter<Board> {

    /**
     * Файл с данным названием хранится в InternalStorage
     */
    private const val BOARDS_FILENAME = "boards.bin"

    var boards = mutableListOf<Board>()
        private set

    var boardsView: IView? = null

    private fun load(context: Context) {
        try {
            val fis = context.openFileInput(BOARDS_FILENAME)
            ObjectInputStream(fis).use {
                boards = it.readObject() as MutableList<Board>
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
            it.writeObject(boards)
        }
    }

    override fun add(board: Board) {
        if (board.title.isBlank()) {
            boardsView?.showError("Название новой доски не должно быть пустым")
            return
        }
        with(board){
            boards.add(this)
            boardsView?.showTasks(this)
        }
    }

    override fun removeAt(pos: Int) {
        if (pos >= 0 && pos < boards.size)
            boards.removeAt(pos)
    }

    fun onClick(pos: Int) {
        if (pos >= 0 && pos < boards.size)
            boardsView?.showTasks(boards[pos])
    }

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