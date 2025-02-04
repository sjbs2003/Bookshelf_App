package com.example.bookshelfapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BookShelfApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@BookShelfApplication)
            modules(
                listOf(
                    viewModelModule,
                    networkModule
                )
            )
        }
    }
}