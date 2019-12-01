package com.project.trello_fintech.di.modules

import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.di.scopes.MainActivityScope
import dagger.Module
import dagger.Provides


@Module
class MainActivityModule(private val mainActivity: MainActivity) {

    @Provides
    @MainActivityScope
    fun getMainActivity() = mainActivity
}