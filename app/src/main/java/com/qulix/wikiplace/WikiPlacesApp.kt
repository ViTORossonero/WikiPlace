package com.qulix.wikiplace

import android.app.Application
import android.os.StrictMode
import com.qulix.wikiplace.di.AppComponent
import timber.log.Timber


class WikiPlacesApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = AppComponent.create(this)

        setupLoggers()
        setupStrictMode()
    }

    companion object {
        private fun setupLoggers() {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        }

        private fun setupStrictMode() {
            if (BuildConfig.DEBUG) {
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectAll()
                        .penaltyLog()
                        .build())
                StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .penaltyDeath()
                        .build())
            }
        }
    }
}