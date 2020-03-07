package com.project.trello_fintech.adapters

import android.graphics.Paint
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.ActionMenuView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.numberprogressbar.NumberProgressBar
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.fragments.TaskDetailFragment
import com.project.trello_fintech.models.Checklist
import com.project.trello_fintech.utils.TrelloUtil
import com.project.trello_fintech.views.TaskDetailSectionView
import javax.inject.Inject
import com.project.trello_fintech.R
import com.project.trello_fintech.listeners.CheckitemTouchHelperCallback


/**
 * Адаптер списков действий для выполнения одной задачи
 * @property fragment TaskDetailFragment
 * @property data List<Checklist>
 */
class ChecklistsAdapter(private val fragment: TaskDetailFragment): RecyclerView.Adapter<ChecklistsAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val sectionView: TaskDetailSectionView = view.findViewById(R.id.title)
        val checklistProgressBar: NumberProgressBar = view.findViewById(R.id.checklist_progress)
        val checkitemsView: RecyclerView = view.findViewById(R.id.checkitems)
    }

    var data: List<Checklist> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checklist_list_item, parent, false)
        return ViewHolder(view).apply { sectionView.inflateMenu(R.menu.checklist_actions) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checklist = data[position]
        with(holder) {
            sectionView.setText(checklist.title)
            sectionView.setOnClickListener {
                checkitemsView.visibility = if (checkitemsView.isShown) View.GONE else View.VISIBLE
            }

            sectionView.menuView.setOnMenuItemClickListener {
                handleSectionViewMenuClick(it, checklist)
                true
            }

            checklistProgressBar.byCheckitemsCheckedCount(checklist.items)

            checkitemsView.layoutManager = LinearLayoutManager(checkitemsView.context)
            checkitemsView.adapter = ChecklistAdapter(checklist, checklistProgressBar, fragment).apply { data = checklist.items }
            ItemTouchHelper(CheckitemTouchHelperCallback(fragment.taskDetailViewModel)).attachToRecyclerView(checkitemsView)
        }
    }

    override fun getItemCount(): Int = data.size

    private fun NumberProgressBar.byCheckitemsCheckedCount(checkitems: List<Checklist.Item>) {
        val checkedCount = checkitems.count { it.isChecked }
        progress = (checkedCount.toFloat() / checkitems.size.toFloat() * 100).toInt()
    }

    private fun handleSectionViewMenuClick(menuItem: MenuItem, checklist: Checklist) {
        when (menuItem.itemId) {
            R.id.add_checkitem -> fragment.showCheckitemDialog(checklist)
            R.id.edit_checklist -> fragment.showChecklistDialog(checklist)
        }
    }
}


/**
 * Адаптер одного списка действий
 * @property checklist Checklist
 * @property checklistProgressBar NumberProgressBar
 * @property fragment TaskDetailFragment
 * @property mainActivity MainActivity
 * @property trelloUtil TrelloUtil
 * @property data List<Item>
 * @property checkedCount Int
 */
class ChecklistAdapter (
        private val checklist: Checklist,
        private val checklistProgressBar: NumberProgressBar,
        private val fragment: TaskDetailFragment
    ): RecyclerView.Adapter<ChecklistAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
        val textView: TextView = view.findViewById(R.id.text)
        val actionsBar: ActionMenuView = view.findViewById(R.id.actions_bar)
        lateinit var checklistId: String
        lateinit var checkitem: Checklist.Item
    }

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var mainActivity: MainActivity

    @Inject
    lateinit var trelloUtil: TrelloUtil

    var data: List<Checklist.Item> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var checkedCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checkitem_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkitem = data[position]
        with (holder) {
            this.checklistId = checklist.id
            this.checkitem = checkitem

            if (trelloUtil.isTaskURL(checkitem)) {
                val (id, text) = trelloUtil.parseTaskUrl(checkitem.title)
                textView.setupTaskCheckitem(id, text)
            } else
                textView.text = checkitem.title

            checkBox.isChecked = checkitem.isChecked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                fragment.taskDetailViewModel.updateCheckitem(checkitem.id, isChecked = isChecked)
                checkedCount += if (isChecked) 1 else -1
                checklistProgressBar.progress = (checkedCount.toFloat() / itemCount.toFloat() * 100).toInt()
            }

            actionsBar.setupCheckitemEditActionMenu(checkitem)
        }
    }

    override fun getItemCount(): Int = data.size

    private fun TextView.setupTaskCheckitem(id: String, text: String) {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG or Paint.FAKE_BOLD_TEXT_FLAG
        setTextColor(resources.getColor(R.color.colorBlack, null))
        val drawable = resources.getDrawable(R.drawable.trello_icon, null).apply {
            setBounds(-10, -10, 50, 50)
        }
        setCompoundDrawables(drawable, null, null, null)
        this.text = text
        setOnClickListener {
            mainActivity.showTaskDetail(id)
        }
    }

    private fun ActionMenuView.setupCheckitemEditActionMenu(checkitem: Checklist.Item) =
        with(menu.add(Menu.NONE, Menu.NONE, 0, "")) {
            setIcon(android.R.drawable.ic_menu_edit)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                fragment.showCheckitemDialog(checklist, checkitem)
                true
            }
        }
}