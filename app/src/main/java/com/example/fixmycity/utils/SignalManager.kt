package com.example.fixmycity.utils

import android.content.Context
import android.widget.Toast

class SignalManager private constructor(context: Context) {

    private val appContext: Context = context.applicationContext
    private var currentToast: Toast? = null

    enum class ToastLength(val length: Int) {
        SHORT(Toast.LENGTH_SHORT),
        LONG(Toast.LENGTH_LONG)
    }

    companion object {
        @Volatile
        private var instance: SignalManager? = null

        fun init(context: Context): SignalManager {
            return instance ?: synchronized(this) {
                instance ?: SignalManager(context).also { instance = it }
            }
        }

        fun getInstance(): SignalManager {
            return instance ?: throw IllegalStateException(
                "SignalManager must be initialized by calling init(context) before use."
            )
        }
    }

    fun toast(text: String, duration: ToastLength = ToastLength.SHORT) {
        currentToast?.cancel()
        currentToast = Toast.makeText(appContext, text, duration.length)
        currentToast?.show()
    }

    fun cancelToast() {
        currentToast?.cancel()
        currentToast = null
    }
}