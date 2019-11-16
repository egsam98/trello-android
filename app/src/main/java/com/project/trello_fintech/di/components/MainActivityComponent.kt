package com.project.trello_fintech.di.components

import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.di.modules.MainActivityModule
import com.project.trello_fintech.di.modules.TrelloApiModule
import com.project.trello_fintech.di.modules.ViewModelsModule
import com.project.trello_fintech.di.scopes.MainActivityScope
import com.project.trello_fintech.fragments.AddBoardDialogFragment
import com.project.trello_fintech.fragments.BoardsFragment
import com.project.trello_fintech.fragments.TasksFragment
import com.project.trello_fintech.fragments.WebViewFragment
import dagger.Subcomponent


/**
 * Компонент с ЖЦ согласно MainActivity
 * @see MainActivity
 */
@Subcomponent(modules = [
    MainActivityModule::class,
    TrelloApiModule::class,
    ViewModelsModule::class
])
@MainActivityScope
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(fragment: BoardsFragment)
    fun inject(fragment: TasksFragment)
    fun inject(fragment: AddBoardDialogFragment)
    fun inject(fragment: WebViewFragment)
}