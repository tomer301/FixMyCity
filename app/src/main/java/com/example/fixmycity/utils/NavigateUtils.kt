package com.example.fixmycity.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.fixmycity.MainActivity

fun AppCompatActivity.navigateToMain() {
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}