package com.example.fixmycity

import android.app.Application
import com.example.fixmycity.utils.SignalManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}