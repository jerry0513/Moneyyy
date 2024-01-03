package com.moneyyy

import android.app.Application
import com.moneyyy.data.database.AppDatabase

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
    }
}