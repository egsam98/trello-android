package com.project.trello_fintech.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.project.trello_fintech.R
import com.project.trello_fintech.databinding.FragmentAttachmentFullScreenBinding
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.views.CircularProgressBar


/**
 * Фрагмент для отображения изображения на весь экран
 * @property binding FragmentAttachmentFullScreenBinding
 * @property progressBar CircularProgressBar
 */
class AttachmentFullScreenFragment: Fragment() {

    private lateinit var binding: FragmentAttachmentFullScreenBinding
    private lateinit var progressBar: CircularProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_attachment_full_screen, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val attachment = arguments?.getSerializable("attachment") as Task.Attachment
        binding.attachment = attachment
        progressBar = view.findViewById<CircularProgressBar>(R.id.progressBar).apply {
            loading()
        }

        Glide.with(requireContext())
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .load(attachment.getImageUrl(Task.AttachmentType.LARGE))
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    view.findViewById<ImageView>(R.id.attachment_large).setImageBitmap(resource)
                    progressBar.done()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}