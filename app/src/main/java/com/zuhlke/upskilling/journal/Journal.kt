package com.zuhlke.upskilling.journal

import android.app.Application
import com.facebook.stetho.Stetho
import timber.log.Timber

class Journal : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}