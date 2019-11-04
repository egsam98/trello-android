package com.project.trello_fintech.utils.reactive

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Событие, которое можно отслеживать, подписавшись
 * @param T
 * @property pending AtomicBoolean для предотвращения повторной подписки на изменения
 */
class LiveEvent<T>: LiveData<T>() {

    private val pending = AtomicBoolean()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    fun emit(t: T? = null) {
        pending.set(true)
        value = t
    }
}