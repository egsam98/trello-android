package com.project.trello_fintech.di.modules

import com.project.trello_fintech.activities.MainActivity
import dagger.Module
import dagger.Provides


@Module
class MainActivityModule(private val mainActivity: MainActivity) {

    @Provides
    fun getMainActivity() = mainActivity
}