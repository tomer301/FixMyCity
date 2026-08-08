package com.example.fixmycity.utils

import android.net.Uri

object ReportValidator {

    fun validateDescription(description: String): String? {
        if (description.isBlank()) return "נא להזין תיאור למפגע"
        if (description.length < 5) return "התיאור קצר מדי (לפחות 5 תווים)"
        if (description.length > 200) return "התיאור ארוך מידי (מקסימום 200 תווים)"
        return null
    }

    fun validateCitySelection(selectedPosition: Int): String? {
        if (selectedPosition <= 0) return "נא לבחור עיר"
        return null
    }

    fun validateNeighborhoodSelection(selectedPosition: Int): String? {
        if (selectedPosition <= 0) return "נא לבחור שכונה"
        return null
    }

    fun validateCategorySelection(selectedPosition: Int): String? {
        if (selectedPosition <= 0) return "נא לבחור קטגוריית מפגע"
        return null
    }

    fun validateImage(imageUri: Uri?): String? {
        if (imageUri == null) return "נא לצרף תמונת מפגע"
        return null
    }

    fun validateAddress(address: String): String {
        return address.trim().ifBlank { "אין כתובת מדויקת" }
    }
}