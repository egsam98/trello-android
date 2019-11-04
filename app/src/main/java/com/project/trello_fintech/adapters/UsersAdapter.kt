package com.project.trello_fintech.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.project.trello_fintech.models.User
import android.view.LayoutInflater
import android.widget.TextView
import com.project.trello_fintech.R
import com.project.trello_fintech.views.AvatarView


/**
 * Адаптер для списка пользователей
 * @see com.project.trello_fintech.fragments.UsersFragment
 * @property cxt Context
 * @property layoutId Int
 */
class UsersAdapter(private val cxt: Context, private val layoutId: Int, data: Array<User>):
    ArrayAdapter<User>(cxt, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view = convertView?: LayoutInflater.from(cxt).inflate(layoutId, parent, false)

        getItem(position)?.let {
            view.findViewById<TextView>(R.id.user_fullname).text = it.fullname
            view.findViewById<AvatarView>(R.id.user_avatar).user = it
        }

        return view
    }
}