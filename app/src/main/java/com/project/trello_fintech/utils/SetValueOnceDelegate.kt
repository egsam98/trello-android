package com.project.trello_fintech.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Изменение экземпляра класса возможно единыжды
 * @param T
 * @property initValue T
 * @property instance T
 */
class SetValueOnceDelegate<T>(private val initValue: T): ReadWriteProperty<Any, T> {

    private var instance = initValue

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return instance
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (instance == initValue) {
            instance = value
        }
    }
}