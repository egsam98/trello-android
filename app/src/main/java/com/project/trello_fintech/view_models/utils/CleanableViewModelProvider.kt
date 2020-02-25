package com.project.trello_fintech.view_models.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.di.scopes.MainActivityScope
import com.project.trello_fintech.view_models.CleanableViewModel
import javax.inject.Inject


/**
 * ViewModelProviders для классов, наслед. CleanableViewModel
 * @property mainActivity MainActivity
 * @property viewModelFactory ViewModelFactory
 */
@MainActivityScope
class CleanableViewModelProvider @Inject constructor(val mainActivity: MainActivity, val viewModelFactory: ViewModelFactory) {
    inline fun <reified T: CleanableViewModel> get(disposableOwner: LifecycleOwner): T {
        val viewModelProvider = ViewModelProviders.of(mainActivity, viewModelFactory)
        return viewModelProvider.get(T::class.java).apply {
            disposableOwner.lifecycle.addObserver(this)
        }
    }
}