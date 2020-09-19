package com.sugaryple.fakelocation

import android.content.Context
import android.location.LocationManager
import androidx.work.WorkManager
import com.google.android.gms.location.LocationServices
import com.sugaryple.fakelocation.feature.fakeGps.FakeGpsWorkManager
import com.sugaryple.fakelocation.feature.fakeGps.FakeGpsNotificationController
import com.sugaryple.fakelocation.helper.MyLocationHelper
import com.sugaryple.fakelocation.maps.MapsViewModel
import com.sugaryple.fakelocation.model.GpsProviderModel
import com.sugaryple.fakelocation.map.MapModel
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
        getViewModels(),
        getManagerModules(),
        getGpsModules(),
        getHelperModules()
    )

    private fun getViewModels() = module {
        viewModel { (mapModel: MapModel) ->
            MapsViewModel(mapModel)
        }
    }

    private fun getManagerModules() = module {
        single { WorkManager.getInstance(androidContext()) }
        single { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    }

    private fun getGpsModules() = module {
        single { FakeGpsWorkManager(get()) }
        single { FakeGpsNotificationController(androidContext()) }
        single { GpsProviderModel(get()) }
    }

    private fun getHelperModules() = module {
        single { MyLocationHelper(LocationServices.getFusedLocationProviderClient(androidContext())) }
    }

}