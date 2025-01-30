package com.basudev.weatherapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentWeather(
    @PrimaryKey val city: String,
    val temperature: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val condition: String,
    val icon: String
)