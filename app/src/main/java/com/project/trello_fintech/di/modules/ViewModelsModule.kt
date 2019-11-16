package com.project.trello_fintech.di.modules

import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.di.scopes.MainActivityScope
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import com.project.trello_fintech.view_models.utils.ViewModelFactory
import dagger.Module
import dagger.Provides


@Module
class ViewModelsModule {

    @Provides
    @MainActivityScope
    fun getViewModelFactory(retrofitClient: RetrofitClient) =
        ViewModelFactory(retrofitClient)

    @Provides
    @MainActivityScope
    fun getCleanableViewModelProvider(mainActivity: MainActivity, viewModelFactory: ViewModelFactory) =
        CleanableViewModelProvider(mainActivity, viewModelFactory)
}