package com.project.homework_2.presenters

import android.content.Context
import android.graphics.Color
import com.project.homework_2.models.Board
import java.io.*
import kotlin.random.Random


/**
 * Презентер для манипуляций над списком досок
 * @property boardsView IView
 * @property boards ArrayList<Board>
 */
class BoardsPresenter(private val boardsView: IView) {

    companion object {
        /**
         * Файл с данным названием хранится в InternalStorage
         */
        private const val BOARDS_FILENAME = "boards.bin"
    }

    var boards: ArrayList<Board> = arrayListOf()

    fun load(context: Context) {
        try {
            val fis = context.openFileInput(BOARDS_FILENAME)
            ObjectInputStream(fis).use {
                boards = it.readObject() as ArrayList<Board>
            }
        } catch (e: FileNotFoundException) {
            // Иначе пустой список
        }
    }

    fun save(context: Context) {
        ObjectOutputStream(context.openFileOutput(BOARDS_FILENAME, Context.MODE_PRIVATE)).use {
            it.writeObject(boards)
        }
    }

    fun addNew(title: String) {
        if (title.isBlank()) {
            boardsView.showError("Название новой доски не должно быть пустым")
            return
        }
        boards.add(Board(title, randomColorId()))
        boardsView.showDetails(title)
    }

    fun removeAt(pos: Int) {
        if (pos >= 0 && pos < boards.size)
            boards.removeAt(pos)
    }

    fun onClick(pos: Int) {
        if (pos >= 0 && pos < boards.size)
            boardsView.showDetails(boards[pos].title)
    }

    private fun randomColorId(): Int {
        val (one, two, three) = IntArray(3) { Random.nextInt(256) }
        return Color.rgb(one, two, three)
    }

    /**
     * Интерфейс, который Activity/Fragment должен реализовать для взаимодействия с презентером
     */
    interface IView {
        /**
         * Отобразить детальную информацию по выбранной на UI доске
         * @param title String
         */
        fun showDetails(title: String)

        /**
         * Сообщение об ошибке
         * @param message String
         */
        fun showError(message: String)
    }
}