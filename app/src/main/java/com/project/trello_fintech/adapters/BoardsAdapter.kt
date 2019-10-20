package com.project.trello_fintech.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.trello_fintech.R
import com.project.trello_fintech.presenters.BoardsPresenter


/**
 * Адаптер RecyclerView, хранящий список досок
 * @property data ArrayList<Board> список досок
 */
class BoardsAdapter: RecyclerView.Adapter<BoardsAdapter.BoardsViewHolder>() {

    inner class BoardsViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.title)
        val imageView: ImageView = view.findViewById(R.id.image)
        val deleteButton: FloatingActionButton = view.findViewById(R.id.delete_board)
    }

    private val data = BoardsPresenter.boards

    override fun onBindViewHolder(holder: BoardsViewHolder, pos: Int) {
        val (title, _, color) = data[pos]

        with(holder) {
            textView.text = title
            val coloredImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
                eraseColor(color)
            }
            imageView.setImageBitmap(coloredImage)

            deleteButton.setOnClickListener {
                BoardsPresenter.removeAt(pos)
                notifyDataSetChanged()
            }

            view.setOnClickListener { BoardsPresenter.onClick(pos) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BoardsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_list_item, parent, false)
        return BoardsViewHolder(view)
    }

    override fun getItemCount() = data.size
}