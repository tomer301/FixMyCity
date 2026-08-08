package com.example.fixmycity.models

import com.example.fixmycity.utils.Constants

data class HazardReport(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val cityName: String = "",
    val neighborhood: String = "",
    val address: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val status: String = Constants.Status.STATUS_RECEIVED,
    var upVotedCount: Int = 0,
    var upVotedUserIds: List<String> = emptyList(),
    val reporterUserId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
