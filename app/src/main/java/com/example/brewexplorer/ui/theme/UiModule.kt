package com.example.brewexplorer.ui.theme

import com.example.brewexplorer.ui.theme.base.MainForm
import com.example.brewexplorer.ui.theme.detail.DetailScreenViewModel
import com.example.brewexplorer.ui.theme.overview.OverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Defines a Koin module for setting up the UI layer of the application.
 * This module configures and provides ViewModel dependencies using Koin.
 */
internal val uiModule = module {
    // Provides an instance of OverviewViewModel, injecting the required dataSource dependency.
    // The viewModel method from Koin is used to declare a ViewModel dependency,
    // ensuring that Koin handles its lifecycle appropriately.
    single {
        MainForm()
    }
    viewModel { OverviewViewModel(dataSource = get()) }
    viewModel { DetailScreenViewModel(forum = get(), savedStateHandle = get())}
}