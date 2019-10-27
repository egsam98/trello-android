package com.project.trello_fintech.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.project.trello_fintech.R
import com.project.trello_fintech.adapters.TasksAdapter
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.presenters.TasksPresenter
import com.project.trello_fintech.views.CircularProgressBar
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.DragItem


/**
 * Фрагмент списка задач (в виде BoardView)
 * @property bucket ImageView мусорное ведро для удаления задач при помощи drag n drop'а
 * @property boardView BoardView
 * @property progressBar CircularProgressBar
 */
class TasksFragment: Fragment() {

    private lateinit var bucket: ImageView
    private lateinit var boardView: BoardView
    private lateinit var progressBar: CircularProgressBar

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
            bucket.visibility = View.GONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = view.findViewById(R.id.progressBar)
        bucket = view.findViewById(R.id.bucket)
        boardView = view.findViewById<BoardView>(R.id.tasks).apply {
            val dragItem = DeletableDragItem(view.context, R.layout.task_list_item)
            setCustomDragItem(dragItem)

            setBoardListener(object: BoardView.BoardListenerAdapter() {
                override fun onItemDragStarted(column: Int, row: Int) {
                    val adapter = this@apply.getAdapter(column) as TasksPresenter.IAdapter
                    adapter.getPresenter().onItemDragStarted(row)
                }

                override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                    val adapter = this@apply.getAdapter(toColumn) as TasksPresenter.IAdapter
                    adapter.getPresenter().onItemDragEnded(toRow)
                }
            })
        }

        val selectedBoard = (arguments?.getSerializable("board") as Board)

        val prefs = selectedBoard.prefs
        if (prefs != null) {
            Glide.with(this@TasksFragment)
                .asBitmap()
                .load(prefs.imageUrls?.last()?.url?: prefs.fromHexColor())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object: CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val drawable = BitmapDrawable(resources, resource)
                        view.findViewById<ConstraintLayout>(R.id.tasks_layout).background = drawable
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }

        for (column in selectedBoard.columns) {
            val presenter = TasksPresenter(column)
            val tasksAdapter = TasksAdapter(presenter)
            with(presenter){
                adapter = tasksAdapter
                observe()
                    .doOnSubscribe { progressBar.loading() }
                    .subscribe {
                        tasksAdapter.itemList = it
                        progressBar.done()
                    }
            }

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

    private fun BoardView.removeFromAllColumnsById(id: String) {
        (0 until columnCount)
            .map { (getAdapter(it) as TasksAdapter).getPresenter() }
            .forEach { it.removeById(id) }
    }
}