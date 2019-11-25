package com.project.trello_fintech.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Task


/**
 * Адаптер для превью-изображений в списках задач одной доски
 * @property data List<Attachment>
 */
class AttachmentsPreviewAdapter(private val data: List<Task.Attachment>):
    RecyclerView.Adapter<AttachmentsPreviewAdapter.ViewHolder>() {

    class ViewHolder(val imageView: ImageView): RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            setPadding(10, 0, 0, 10)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val attachment = data[position]
            when (attachment.isImage()) {
                true -> Glide.with(imageView.context)
                    .asBitmap()
                    .load(attachment.getImageUrl(Task.AttachmentType.SMALL))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            imageView.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
                false -> imageView.background = imageView.context.getDrawable(R.color.colorGrey)
            }
        }
    }

    override fun getItemCount() = data.size
}