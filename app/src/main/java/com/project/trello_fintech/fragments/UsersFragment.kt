package com.project.trello_fintech.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.ListFragment
import com.project.trello_fintech.presenters.UsersPresenter
import com.project.trello_fintech.R
import com.project.trello_fintech.adapters.UsersAdapter


/**
 * Фрагмент списка пользователей
 */
class UsersFragment: ListFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        UsersPresenter.createMocks(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        ListView(inflater.context)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = UsersAdapter(
            view.context,
            R.layout.user_list_item,
            UsersPresenter.users
        )
    }
}