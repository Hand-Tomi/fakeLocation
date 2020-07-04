package com.sugaryple.fakelocation

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyKoin {
    fun start(applicationContext: Context) {
        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(getModules())
        }
    }

    private fun getModules() = module {
        getViewModels()
    }

    private fun getViewModels() = module {
        viewModel { MapsViewModel() }
    }

}