package com.sugaryple.fakelocation

import android.content.Context
import com.sugaryple.fakelocation.maps.MapsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.dsl.module

class MyKoin {
    fun start(applicationContext: Context) {
        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            } else {
                EmptyLogger()
            }
            androidContext(applicationContext)
            modules(getModules())
        }
    }

    private fun getModules() = listOf(
        getViewModels()
    )

    private fun getViewModels() = module {
        viewModel { MapsViewModel() }
    }

}