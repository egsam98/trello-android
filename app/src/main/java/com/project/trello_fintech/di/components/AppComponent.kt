package com.project.trello_fintech.di.components

import com.project.trello_fintech.di.modules.*
import dagger.Component
import javax.inject.Singleton

/**
 * Компонент, предоставляющий долгоживущие классы (ЖЦ согласно ApplicationContext)
 */
@Component(modules = [AppContextModule::class])
@Singleton
interface AppComponent {
    fun plusMainActivityComponent(mainActivityModule: MainActivityModule): MainActivityComponent
}