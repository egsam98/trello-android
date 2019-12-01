package com.project.trello_fintech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.trello_fintech.R
import com.project.trello_fintech.models.User
import com.project.trello_fintech.views.AvatarView
import io.reactivex.subjects.PublishSubject

class SelectParticipantsAdapter: RecyclerView.Adapter<SelectParticipantsAdapter.ViewHolder>() {

    private var boardAndTaskUsers = mutableMapOf<String, List<User>>("board" to listOf(), "task" to listOf())
    val onUserSelect = PublishSubject.create<User>()
    private var isSelection = false

    fun attachBoardAndTaskUsers(pair: Pair<List<User>, List<User>>) {
        val (boardUsers, taskUsers) = pair
        boardAndTaskUsers["board"] = boardUsers
        boardAndTaskUsers["task"] = taskUsers
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val avatarView: AvatarView = view.findViewById(R.id.avatar)
        val usernameView: TextView = view.findViewById(R.id.username)
        val selected: CheckBox = view.findViewById(R.id.selected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_participants_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val boardUsers = boardAndTaskUsers.getValue("board")
        val taskUsers = boardAndTaskUsers.getValue("task")
        val user = boardUsers[position]
        with(holder) {
            avatarView.user = user
            usernameView.text = user.fullname
            taskUsers.find { it.id == user.id }?.let {
                selected.isChecked = true
            }
            view.setOnClickListener {
                when (selected.isChecked) {
                    true -> selected.isChecked = false
                    false -> selected.isChecked = true
                }
            }

            if (isSelection) {
                if (selected.isChecked) {
                    onUserSelect.onNext(user)
                }
            }
        }
    }

    override fun getItemCount() = boardAndTaskUsers.getValue("board").size

    fun getSelected() {
        isSelection = true
        notifyDataSetChanged()
    }
}