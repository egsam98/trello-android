package com.project.trello_fintech.utils

import io.reactivex.subjects.BehaviorSubject
import java.io.Serializable


/**
 * Список, на изменения которого можно подписаться
 * @param T: Serializable
 * @property observable BehaviorSubject<(kotlin.collections.MutableList<T>..kotlin.collections.MutableList<T>?)>
 * @property data MutableList<T>
 */
class LiveList<T: Serializable> {
    private val observable = BehaviorSubject.create<MutableList<T>>()

    var data = mutableListOf<T>()
        set(list) {
            field = list
            observable.onNext(field)
        }

    fun observe() = observable

    infix fun add(elem: T) {
        data.add(elem)
        observable.onNext(data)
    }

    fun add(index: Int, elem: T) {
        data.add(index, elem)
        observable.onNext(data)
    }

    fun move(source: T, target: T) {
        val targetIndex = data.indexOf(target)
        data.remove(source)
        data.add(targetIndex, source)
        observable.onNext(data)
    }

    infix fun remove(elem: T) {
        data.remove(elem)
        observable.onNext(data)
    }
}