package com.project.trello_fintech.listeners

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.project.trello_fintech.adapters.BoardStrategy
import com.project.trello_fintech.models.IListItem
import com.project.trello_fintech.view_models.BoardsViewModel


/**
 * ItemTouchHelper.Callback для отслеживания перемещения досок в списке и удаления при помощи свайпа
 * @property viewModel BoardsViewModel
 */
class BoardTouchHelperCallback(private val viewModel: BoardsViewModel): ItemTouchHelper.Callback() {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        viewModel.remove((viewHolder as BoardStrategy.ViewHolder).board)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) =
        when(viewHolder.itemViewType) {
            IListItem.BODY -> {
                val dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeDirs = ItemTouchHelper.START or ItemTouchHelper.END
                makeMovementFlags(dragDirs, swipeDirs)
            }
            else -> makeMovementFlags(0, 0)
        }

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        viewModel.move(
            (source as BoardStrategy.ViewHolder).board,
            (target as BoardStrategy.ViewHolder).board
        )

        return true
    }

    override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder,
                             target: RecyclerView.ViewHolder): Boolean =
        target.itemViewType == IListItem.BODY
}