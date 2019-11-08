package com.project.trello_fintech.view_models

import androidx.lifecycle.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * ViewModel, очищающая CompositeDisposable при каждм вызове onDestroy у Fragmnet/FragmentActivity
 * @property disposables CompositeDisposable
 */
abstract class CleanableViewModel: ViewModel(), LifecycleObserver {
    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        disposables.clear()
    }

    override fun onCleared() {
        super.onCleared()
        onDestroy()
    }

    fun clearOnDestroy(disposable: Disposable) {
        disposables.add(disposable)
    }
}