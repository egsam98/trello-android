package com.project.trello_fintech.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.project.trello_fintech.R
import com.project.trello_fintech.listeners.BoardsChangeCallback
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.IListItem
import com.project.trello_fintech.view_models.BoardsViewModel


/**
 * Адаптер RecyclerView, хранящий список досок
 * @property viewModel BoardsViewModel
 * @property differ AsyncListDiffer<IListItem> для расчета разницы между предыдущим и текущим списком в бэкграунд потоке
 */
class BoardsAdapter(private val viewModel: BoardsViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer<IListItem>(this, BoardsChangeCallback)

    fun setData(data: List<IListItem>) {
        differ.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        getStrategy(viewType).onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        getStrategy(getItemViewType(position)).onBindViewHolder(holder, differ.currentList[position])

    private fun getStrategy(viewType: Int) = when(viewType) {
        IListItem.BODY -> BoardStrategy(viewModel)
        IListItem.HEADER -> CategoryStrategy
        IListItem.NOTHING -> NothingStrategy
        else -> throw IllegalArgumentException(
            "ViewType must be any of ${IListItem.HEADER}, ${IListItem.BODY} or ${IListItem.NOTHING}")
    }

    override fun getItemViewType(position: Int) = differ.currentList[position].getType()
    override fun getItemCount() = differ.currentList.size
}

/**
 * В зависимости от ItemViewType используется определенный ViewHolder - реализуется стратегия
 */
interface ViewHolderStrategy {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IListItem)
}

class BoardStrategy(private val viewModel: BoardsViewModel): ViewHolderStrategy {

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
        with(holder as ViewHolder) {
            board = item as Board
            view.setOnClickListener{ viewModel.onClick(board) }
            textView.text = board.title
            val prefs = board.prefs
            if (prefs != null) {
                Glide.with(view.context)
                    .asBitmap()
                    .load(prefs.imageUrls?.first()?.url?: prefs.fromHexColor())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions().override(100, 100))
                    .into(imageView)
            }
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