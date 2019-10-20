package com.project.homework_2.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.project.homework_2.models.User
import android.view.LayoutInflater
import android.widget.TextView
import com.project.homework_2.R
import com.project.homework_2.views.AvatarView


/**
 * Адаптер для списка пользователей
 * @see com.project.homework_2.fragments.UsersFragment
 * @property cxt Context
 * @property layoutId Int
 */
class UsersAdapter(private val cxt: Context, private val layoutId: Int, data: Array<User>):
    ArrayAdapter<User>(cxt, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view = convertView?: LayoutInflater.from(cxt).inflate(layoutId, parent, false)

        getItem(position)?.let { user ->
            with(view) {
                findViewById<TextView>(R.id.user_fullname).text = user.fullname
                findViewById<AvatarView>(R.id.user_avatar).user = user
            }
        }

        return view
    }
}