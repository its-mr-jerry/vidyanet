package com.kastack.vidyanet

import android.app.Application
import com.kastack.vidyanet.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class VidyaNetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@VidyaNetApp)
        }
    }
}
