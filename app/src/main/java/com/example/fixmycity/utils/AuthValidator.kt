package com.example.fixmycity.utils

import android.util.Patterns

object AuthValidator {
    fun validateEmail(email: String): String? {
        if (email.isEmpty()) return "נא להזין דואר אלקטרוני"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "כתובת דואר אלקטרוני לא תקינה"
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isEmpty()) return "נא להזין סיסמה"
        if (password.length < 6) return "הסיסמא חייבת להכיל לפחות 6 תווים"
        return null
    }

    fun validatePasswordConfirm(password: String, confirm: String): String? {
        if (password != confirm) return "סיסמאות אינן תואמות"
        return null
    }
}