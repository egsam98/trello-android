package com.project.homework_2.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.homework_2.R
import com.project.homework_2.presenters.BoardsPresenter


/**
 * Адаптер RecyclerView, хранящий список досок
 * @see com.project.homework_2.fragments.BoardsFragment.boardsRecyclerView
 * @property presenter BoardsPresenter
 * @property data ArrayList<Board> список досок
 */
class BoardsAdapter(private val presenter: BoardsPresenter): RecyclerView.Adapter<BoardsAdapter.BoardsViewHolder>() {

    inner class BoardsViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.title)
        val imageView: ImageView = view.findViewById(R.id.image)
        val deleteButton: FloatingActionButton = view.findViewById(R.id.delete_board)
    }

    private val data = presenter.boards

    override fun onBindViewHolder(holder: BoardsViewHolder, pos: Int) {
        val (title, color) = data[pos]

        with(holder) {
            textView.text = title
            val coloredImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
                eraseColor(color)
            }
            imageView.setImageBitmap(coloredImage)

            deleteButton.setOnClickListener {
                presenter.removeAt(pos)
                notifyDataSetChanged()
            }

            view.setOnClickListener { presenter.onClick(pos) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BoardsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_view, parent, false)
        return BoardsViewHolder(view)
    }

    override fun getItemCount() = data.size
}