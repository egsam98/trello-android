package com.project.trello_fintech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.project.trello_fintech.models.User
import com.project.trello_fintech.views.AvatarView
import com.project.trello_fintech.R
import com.project.trello_fintech.fragments.TaskDetailFragment


/**
 *
 * @property data List<User>
 */
class ParticipantsAdapter(private val taskDetailFragment: TaskDetailFragment): RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

    var data = listOf<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(val avatarView: AvatarView): RecyclerView.ViewHolder(avatarView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(AvatarView(parent.context, null).apply {
            layoutParams = ViewGroup.MarginLayoutParams(150, 150).apply {
                setMargins(10, 0, 0, 0)
            }
        })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.avatarView){
            user = data[position]
            setOnClickListener { taskDetailFragment.selectParticipants() }
        }
    }

    override fun getItemCount() = data.size
}