package com.evr.tes

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this@App
        appContext = applicationContext

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
