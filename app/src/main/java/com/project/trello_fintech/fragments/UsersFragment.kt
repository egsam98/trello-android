package com.project.trello_fintech.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.project.trello_fintech.view_models.UsersViewModel
import com.project.trello_fintech.R
import com.project.trello_fintech.adapters.UsersAdapter


/**
 * Фрагмент списка пользователей
 * @property usersViewModel UsersViewModel
 */
class UsersFragment: ListFragment() {

    private val usersViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(UsersViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        usersViewModel.createMocks(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        ListView(inflater.context)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        usersViewModel.users.observe(this, Observer {
//            listAdapter = UsersAdapter(view.context, R.layout.user_list_item, it)
//        })
    }
}