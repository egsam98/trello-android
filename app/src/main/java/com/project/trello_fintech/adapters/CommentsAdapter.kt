package com.project.trello_fintech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Comment
import com.project.trello_fintech.utils.toDefaultFormat
import com.project.trello_fintech.views.AvatarView


/**
 * Адаптер комментариев
 * @property data List<Comment>
 */
class CommentsAdapter: RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val avatarView: AvatarView = view.findViewById(R.id.avatar)
        val authorView: TextView = view.findViewById(R.id.author)
        val dateView: TextView = view.findViewById(R.id.date)
        val textView: TextView = view.findViewById(R.id.text)
    }

    var data = listOf<Comment>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        parent.invalidate()
        parent.requestLayout()
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = data[position]
        holder.avatarView.user = comment.author
        holder.authorView.text = comment.author?.fullname
        holder.dateView.text = comment.date.toDefaultFormat()
        holder.textView.text = comment.text
    }

    override fun getItemCount() = data.size
}