package com.example.fixmycity

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.fixmycity.utils.SignalManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        SignalManager.init(this)
    }
}