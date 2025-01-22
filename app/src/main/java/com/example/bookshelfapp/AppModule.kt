package com.example.bookshelfapp

import com.example.bookshelfapp.model.NetworkRepository
import com.example.bookshelfapp.model.Repository
import com.example.bookshelfapp.network.ApiServiceFactory
import com.example.bookshelfapp.viewModel.DetailViewModel
import com.example.bookshelfapp.viewModel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { ApiServiceFactory.createGoogleBooksService() }
    single { ApiServiceFactory.createOpenLibraryService() }
    single<Repository> { NetworkRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
    viewModel { parameters ->
        DetailViewModel(
            get(),
            parameters.get(), // volumeId
            parameters.get()  // isGoogleBooks
        )
    }
}