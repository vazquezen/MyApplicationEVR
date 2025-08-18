package com.evr.tes

import android.app.Application
import android.content.Context
import com.evr.tes.core_data.recaptcha.RecaptchaRepository
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
        RecaptchaRepository.initializeClient(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}