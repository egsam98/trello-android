package com.project.trello_fintech.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.views.AvatarView
import java.text.DateFormat


/**
 * Адаптер, предоставляющий историю изменений задачи
 * @property data List<History>
 */
class TaskHistoryAdapter: RecyclerView.Adapter<TaskHistoryAdapter.ViewHolder>() {

    var data: List<Task.History> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val avatarView: AvatarView = view.findViewById(R.id.user_avatar)
        val messageView: TextView = view.findViewById(R.id.text)
        val attachmentView: ImageView = view.findViewById(R.id.attachment_image)
        val dateView: TextView = view.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = data[position]
        with(holder) {
            avatarView.user = history.creator
            messageView.text = history.message
            dateView.text = DateFormat.getDateInstance().format(history.date)

            if (history.type == "addAttachmentToCard") {
                Glide.with(avatarView)
                    .asBitmap()
                    .load(history.data.attachment?.url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object: CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            attachmentView.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }
    }

    override fun getItemCount() = data.size
}