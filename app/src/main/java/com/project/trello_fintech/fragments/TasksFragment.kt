package com.project.trello_fintech.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import com.project.trello_fintech.BR
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.TasksAdapter
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.utils.ResettableFilterable
import com.project.trello_fintech.view_models.TasksViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.DragItem
import javax.inject.Inject
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.GanttChartActivity
import com.project.trello_fintech.listeners.OnTaskSearchListener
import com.project.trello_fintech.views.ClearableSearchView


/**
 * Фрагмент списка задач (в виде BoardView)
 * @property activity MainActivity
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property tasksViewModel TasksViewModel
 * @property boardView BoardView?
 */
class TasksFragment: Fragment(), NavigationView.OnNavigationItemSelectedListener, DrawerMenuOwner {

    companion object {
        private const val BOARD_ARG = "board"
        fun create(board: Board): TasksFragment {
            val bundle = Bundle().apply { putSerializable(BOARD_ARG, board) }
            return TasksFragment().apply { arguments = bundle }
        }
    }

    @Inject
    lateinit var activity: MainActivity

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private lateinit var tasksViewModel: TasksViewModel
    private var boardView: BoardView? = null

    /**
     * Обеспечивает удаление элемента столбца через drag n drop на картинку мусорного ведра внизу экрана
     * @property bucket ImageView
     */
    inner class DeletableDragItem(context: Context, layoutId: Int, private val bucket: ImageView): DragItem(context, layoutId) {
        override fun onEndDragAnimation(dragView: View) {
            val lowerBorder = boardView!!.height - bucket.height - dragView.height
            if (dragView.y > lowerBorder) {
                dragView.visibility = View.GONE
                tasksViewModel.removeFromAllColumnsById()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        MainActivity.component.inject(this)
        tasksViewModel = cleanableViewModelProvider.get(viewLifecycleOwner)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.fragment_tasks, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.setVariable(BR.viewModel, tasksViewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bucket = view.findViewById<ImageView>(R.id.bucket)
        boardView = view.findViewById<BoardView>(R.id.tasks).apply {
            val dragItem = DeletableDragItem(view.context, R.layout.task_list_item, bucket)
            setCustomDragItem(dragItem)

            setBoardListener(object: BoardView.BoardListenerAdapter() {
                override fun onItemDragStarted(column: Int, row: Int) {
                    val adapter = this@apply.getAdapter(column) as TasksAdapter
                    tasksViewModel.onItemDragStarted(adapter.column, row)
                }

                override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                    val fromAdapter = this@apply.getAdapter(fromColumn) as TasksAdapter
                    val toAdapter = this@apply.getAdapter(toColumn) as TasksAdapter
                    tasksViewModel.onItemDragEnded(fromAdapter.column, toAdapter.column, toRow)
                }
            })
        }

        val selectedBoard = requireArguments().getSerializable(BOARD_ARG) as Board

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
            processTaskListItem(column)
        }

        tasksViewModel.observeOnLoaded(selectedBoard.columns.size, viewLifecycleOwner) {
            activity.navigationView.setupMenu()
        }

        activity.supportActionBar?.title = selectedBoard.title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity.navigationView.menu.clear()
        boardView = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val title = resources.getString(R.string.task_search)

        val filters = (0 until boardView!!.columnCount).map { columnInd ->
            val adapter = boardView!!.getAdapter(columnInd) as ResettableFilterable
            adapter.filter
        }

        with(menu.add(Menu.NONE, Menu.NONE, 0, title)){
            setIcon(android.R.drawable.ic_menu_search)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            actionView = ClearableSearchView(context).apply {
                queryHint = title
                onClear = { filters.forEach { it.reset() } }
                setOnQueryTextListener(OnTaskSearchListener(context, filters))
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.gantt_chart)
            GanttChartActivity.start(requireActivity(), tasksViewModel.tasks)
        return true
    }

    private fun processTaskListItem(column: Column) {
        val tasksAdapter = TasksAdapter(column, tasksViewModel, maxAttachmentsPreviewNum = 2)
        tasksViewModel.load(column)
        tasksViewModel.observe(column) {
            tasksAdapter.data = it
        }

        val headerView = LayoutInflater.from(context).inflate(R.layout.task_list_header, null).apply {
            findViewById<TextView>(R.id.task_header_title).text = column.title
            findViewById<ImageButton>(R.id.add_task).setOnClickListener {
                showAddTaskDialog(column)
            }
        }

        boardView!!.addColumn(tasksAdapter, headerView, null, false)
    }

    private fun showAddTaskDialog(column: Column) {
        val fragment = AddTaskDialogFragment.create(column)
        fragment.show(childFragmentManager, null)
    }

    override fun NavigationView.setupMenu() {
        menu.clear()
        inflateMenu(R.menu.tasks)
        setNavigationItemSelectedListener(this@TasksFragment)
    }
}