package com.example.fixmycity.utils

import android.content.Context
import android.widget.ArrayAdapter
import com.example.fixmycity.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView

object CityNeighborhoodHelper {
    fun getNeighborhoodsForCity(context: Context, cityIndex: Int): Array<String> {
        val typedArray = context.resources.obtainTypedArray(R.array.city_neighborhood_arrays)
        val neighborhoodArrayResId = typedArray.getResourceId(cityIndex, R.array.default_neighborhoods)
        typedArray.recycle()
        return context.resources.getStringArray(neighborhoodArrayResId)
    }

    fun updateNeighborhoodDropdown(
        context: Context,
        autoCompleteTextView: MaterialAutoCompleteTextView,
        cityIndex: Int
    ) {
        val neighborhoods = getNeighborhoodsForCity(context, cityIndex)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, neighborhoods)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setText("", false)
    }
}