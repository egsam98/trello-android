package com.project.trello_fintech.adapters

import android.graphics.Bitmap
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.IListItem
import com.project.trello_fintech.presenters.BoardsPresenter


/**
 * Адаптер RecyclerView, хранящий список досок
 * @property data List<IListItem> список элементов (досок и их категорий)
 */
class BoardsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = listOf<IListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        getStrategy(viewType).onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        getStrategy(getItemViewType(position)).onBindViewHolder(holder, data[position])

    private fun getStrategy(viewType: Int) = when(viewType) {
        IListItem.BODY -> BoardStrategy
        IListItem.HEADER -> CategoryStrategy
        IListItem.NOTHING -> NothingStrategy
        else -> throw IllegalArgumentException(
            "ViewType must be any of ${IListItem.HEADER}, ${IListItem.BODY} or ${IListItem.NOTHING}")
    }

    override fun getItemViewType(position: Int) = data[position].getType()
    override fun getItemCount() = data.size
}

/**
 * В зависимости от ItemViewType используется определенный ViewHolder - реализуется стратегия
 */
interface ViewHolderStrategy {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IListItem)
}

object BoardStrategy: ViewHolderStrategy {

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        lateinit var board: Board
        val textView: TextView = view.findViewById(R.id.title)
        val imageView: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IListItem) {
        val (title, _, color) = item as Board

        with(holder as ViewHolder) {
            board = item
            textView.text = title
            val coloredImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
                eraseColor(color)
            }
            imageView.setImageBitmap(coloredImage)
            view.setOnClickListener { BoardsPresenter.onClick(item) }
        }
    }
}

object CategoryStrategy: ViewHolderStrategy {

    class ViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(TextView(parent.context).apply {
            typeface = Typeface.DEFAULT_BOLD
        })

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IListItem) {
        with(holder as ViewHolder) {
            val category = item as Board.Category
            textView.text = category.toString()
        }
    }
}

/**
 * Для пустого списка
 */
object NothingStrategy: ViewHolderStrategy {

    class ViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ViewHolder(TextView(parent.context))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IListItem) {
        with(holder as ViewHolder) {
            textView.text = "Пусто"
        }
    }
}