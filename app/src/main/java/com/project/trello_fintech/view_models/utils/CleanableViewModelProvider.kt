package com.project.trello_fintech.view_models.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.project.trello_fintech.view_models.CleanableViewModel
import java.lang.IllegalArgumentException


/**
 * ViewModelProviders для классов, наслед. CleanableViewModel
 */
object CleanableViewModelProvider {
    inline fun <reified T: CleanableViewModel> get(owner: LifecycleOwner): T {
        val viewModelProvider = when (owner) {
            is Fragment -> ViewModelProviders.of(owner)
            is FragmentActivity -> ViewModelProviders.of(owner)
            else -> throw IllegalArgumentException("owner must be Fragment or FragmentActivity")
        }
        return viewModelProvider.get(T::class.java).apply {
            owner.lifecycle.addObserver(this)
        }
    }
}