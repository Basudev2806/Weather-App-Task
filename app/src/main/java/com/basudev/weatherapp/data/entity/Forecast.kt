package com.basudev.weatherapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Forecast(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val city: String,
    val date: String,
    val minTemp: Double,
    val maxTemp: Double,
    val condition: String,
    val icon: String
)