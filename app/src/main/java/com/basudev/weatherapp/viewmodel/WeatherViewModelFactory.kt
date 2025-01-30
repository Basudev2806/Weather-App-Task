package com.basudev.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.basudev.weatherapp.repository.WeatherRepository

class WeatherViewModelFactory(private val weatherRepository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            WeatherViewModel(weatherRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}