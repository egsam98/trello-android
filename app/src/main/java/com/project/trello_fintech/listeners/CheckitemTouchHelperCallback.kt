package com.project.trello_fintech.listeners

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.project.trello_fintech.adapters.ChecklistAdapter
import com.project.trello_fintech.view_models.TaskDetailViewModel


/**
 * Swipe-эффект для удаления элемента чек-листа
 * @property taskDetailViewModel TaskDetailViewModel
 */
class CheckitemTouchHelperCallback(private val taskDetailViewModel: TaskDetailViewModel):
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val vh = viewHolder as ChecklistAdapter.ViewHolder
        taskDetailViewModel.deleteCheckitem(vh.checklistId, vh.checkitem.id)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean = false
}