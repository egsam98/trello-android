package com.project.homework_2.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageButton
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


/**
 * Фрагмент списка задач (в виде BoardView)
 */
class TasksFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val selectedBoard = arguments?.getSerializable("board") as Board
        val boardView = view.findViewById<BoardView>(R.id.tasks)

        for (column in selectedBoard.columns) {
            val presenter = TasksPresenter(column.tasks)
            val tasksAdapter = TasksAdapter(presenter)
            presenter.iView = tasksAdapter

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
}