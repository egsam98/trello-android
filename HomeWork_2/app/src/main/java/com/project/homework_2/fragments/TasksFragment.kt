package com.project.homework_2.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.project.homework_2.R
import com.project.homework_2.adapters.TasksAdapter
import com.project.homework_2.models.Board
import com.project.homework_2.models.Task
import com.project.homework_2.presenters.BoardsPresenter
import com.project.homework_2.presenters.TasksPresenter
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.DragItem


/**
 * Фрагмент списка задач (в виде BoardView)
 * @property bucket ImageView мусорное ведро для удаления задач при помощи drag n drop'а
 */
class TasksFragment: Fragment() {

    private lateinit var bucket: ImageView
    private lateinit var boardView: BoardView

    /**
     * Обеспечивает удаление элемента столбца через drag n drop на картинку мусорного ведра внизу экрана
     * @property bucket ImageButton
     */
    inner class DeletableDragItem(context: Context, layoutId: Int): DragItem(context, layoutId) {

        override fun onStartDragAnimation(dragView: View) {
            bucket.visibility = View.VISIBLE
        }

        override fun onEndDragAnimation(dragView: View) {
            TasksPresenter.currentTaskId?.let {
                val lowerBorder = boardView.height - bucket.height - dragView.height
                if (dragView.y > lowerBorder) {
                    dragView.visibility = View.GONE
                    boardView.removeFromAllColumnsById(it)
                }
            }

            TasksPresenter.onItemDragEnded()
            bucket.visibility = View.GONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bucket = view.findViewById(R.id.bucket)
        boardView = view.findViewById<BoardView>(R.id.tasks).apply {
            val dragItem = DeletableDragItem(view.context, R.layout.task_list_item)
            setCustomDragItem(dragItem)

            setBoardListener(object: BoardView.BoardListenerAdapter() {
                override fun onItemDragStarted(column: Int, row: Int) {
                    val adapter = this@apply.getAdapter(column)
                    if (adapter is TasksPresenter.IAdapter) {
                        adapter.getPresenter().onItemDragStarted(column, row)
                    }
                }
            })
        }

        val selectedBoard = arguments?.getSerializable("board") as Board

        for (column in selectedBoard.columns) {
            val presenter = TasksPresenter(column.tasks)
            val tasksAdapter = TasksAdapter(presenter)
            presenter.adapter = tasksAdapter

            val headerView = LayoutInflater.from(context).inflate(R.layout.task_list_header, null)
                .apply {
                    findViewById<TextView>(R.id.task_header_title).text = column.title
                    findViewById<ImageButton>(R.id.add_task).setOnClickListener {
                        presenter.add(Task())
                    }
                }

            boardView.addColumn(tasksAdapter, headerView, null, false)
        }

        (activity as AppCompatActivity).supportActionBar?.title = selectedBoard.title
    }

    override fun onPause() {
        super.onPause()
        BoardsPresenter.save(context!!)
    }

    private fun BoardView.removeFromAllColumnsById(id: Long) {
        (0 until columnCount)
            .map { (getAdapter(it) as TasksAdapter).getPresenter() }
            .forEach { it.removeById(id) }
    }
}