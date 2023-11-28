package com.example.firebasecourse

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//We are telling the application that dagger hilt is to perform the dependency injection purpose
@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}