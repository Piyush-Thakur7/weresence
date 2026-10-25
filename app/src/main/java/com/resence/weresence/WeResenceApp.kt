package com.resence.weresence

import android.app.Application
import android.util.Log

class WeResenceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("WeResenceApp", "WeResence Initialized. Sovereign Privacy Active.")
    }
}
