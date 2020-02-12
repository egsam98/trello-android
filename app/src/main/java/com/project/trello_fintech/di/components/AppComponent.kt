package com.project.trello_fintech.di.components

import android.content.Context
import com.project.trello_fintech.di.modules.*
import com.project.trello_fintech.view_models.BoardsViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Компонент, предоставляющий долгоживущие классы (ЖЦ согласно ApplicationContext)
 */
@Component(modules = [AppContextModule::class])
@Singleton
interface AppComponent {
    val context: Context
    fun plusMainActivityComponent(mainActivityModule: MainActivityModule): MainActivityComponent

    fun inject(boardsViewModel: BoardsViewModel)
}