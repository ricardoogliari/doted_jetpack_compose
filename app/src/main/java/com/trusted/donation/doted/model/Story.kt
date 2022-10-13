package com.trusted.donation.doted.model

data class Story(
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val snippet: String,
    val agree: Int,
    val disagree: Int,
    var distance: String = "...")