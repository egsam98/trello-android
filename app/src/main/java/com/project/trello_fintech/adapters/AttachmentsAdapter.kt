package com.project.trello_fintech.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.view_models.TaskDetailViewModel
import java.lang.IllegalArgumentException


/**
 * Адаптер для вложений на странице задачи
 * @property taskDetailViewModel TaskDetailViewModel
 * @property data List<Attachment>
 */
class AttachmentsAdapter(private val taskDetailViewModel: TaskDetailViewModel):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = listOf<Task.Attachment>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private fun getStrategy(viewType: Int) = when (viewType) {
        1 -> ImageStrategy
        0 -> NonImageStrategy
        else -> throw IllegalArgumentException("View type must be any of: 0, 1")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getStrategy(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getStrategy(getItemViewType(position)).onBindViewHolder(holder, taskDetailViewModel, data[position])
    }

    override fun getItemViewType(position: Int): Int = data[position].isImage().toInt()
    override fun getItemCount() = data.size

    private fun Boolean.toInt() = if (this) 1 else 0
}

/**
 * В зависимости от ItemViewType используется определенный ViewHolder - реализуется стратегия
 */
interface AttachmentViewHolderStrategy {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, taskDetailViewModel: TaskDetailViewModel, attachment: Task.Attachment)
}

object ImageStrategy: AttachmentViewHolderStrategy {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.attachment)
        val actions: Toolbar = view.findViewById(R.id.attachment_actions)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.attachment_image_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, taskDetailViewModel: TaskDetailViewModel, attachment: Task.Attachment) {
        with(holder as ViewHolder) {
            Glide.with(imageView.context)
                .asBitmap()
                .load(attachment.getImageUrl(Task.AttachmentType.MEDIUM))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

            imageView.setOnClickListener {
                taskDetailViewModel.onAttachmentClick.emit(attachment)
            }

            actions.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_attachment -> taskDetailViewModel.removeAttachment(attachment)
                }
                true
            }
        }
    }
}

object NonImageStrategy: AttachmentViewHolderStrategy {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val actions: Toolbar = view.findViewById(R.id.attachment_actions)
        val textView: TextView = view.findViewById(R.id.attachment_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.attachment_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, taskDetailViewModel: TaskDetailViewModel, attachment: Task.Attachment) {
        with(holder as ViewHolder) {
            textView.text = attachment.name

            actions.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_attachment -> taskDetailViewModel.removeAttachment(attachment)
                }
                true
            }
        }
    }
}