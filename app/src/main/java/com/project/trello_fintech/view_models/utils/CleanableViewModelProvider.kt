package com.project.trello_fintech.view_models.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.project.trello_fintech.view_models.CleanableViewModel
import java.lang.IllegalArgumentException


/**
 * ViewModelProviders для классов, наслед. CleanableViewModel
 * @property owner LifecycleOwner
 * @property viewModelFactory ViewModelFactory
 */
class CleanableViewModelProvider(val owner: LifecycleOwner, val viewModelFactory: ViewModelFactory) {
    inline fun <reified T: CleanableViewModel> get(disposableOwner: LifecycleOwner): T {
        val viewModelProvider = when (owner) {
            is Fragment -> ViewModelProviders.of(owner, viewModelFactory)
            is FragmentActivity -> ViewModelProviders.of(owner, viewModelFactory)
            else -> throw IllegalArgumentException("owner must be Fragment or FragmentActivity")
        }
        return viewModelProvider.get(T::class.java).apply {
            disposableOwner.lifecycle.addObserver(this)
        }
    }
}