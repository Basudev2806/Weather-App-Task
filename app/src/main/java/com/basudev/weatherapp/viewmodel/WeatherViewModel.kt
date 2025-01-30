package com.basudev.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basudev.weatherapp.data.entity.CurrentWeather
import com.basudev.weatherapp.data.entity.Forecast
import com.basudev.weatherapp.data.model.LocationSearchResult
import com.basudev.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather> = _currentWeather

    private val _forecast = MutableLiveData<List<Forecast>>()
    val forecast: LiveData<List<Forecast>> = _forecast

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _location = MutableLiveData<Pair<Double, Double>>()

    fun updateLocation(lat: Double, lon: Double) {
        _location.value = Pair(lat, lon)
        loadWeatherData()
    }

    private fun loadWeatherData() {
        _location.value?.let { (lat, lon) ->
            viewModelScope.launch {
                try {
                    _currentWeather.value = repository.getCurrentWeather(lat, lon)
                    _forecast.value = repository.getFiveDayForecast(lat, lon)
                } catch (e: Exception) {
                    // Handle error
                    _errorMessage.value = "Something went wrong. Please try again."
                }
            }
        }
    }


    private val _searchResults = MutableLiveData<List<LocationSearchResult>>()
    val searchResults: LiveData<List<LocationSearchResult>> = _searchResults

    fun searchLocations(query: String) {
        viewModelScope.launch {
            try {
                _searchResults.value = repository.searchLocations(query)
                if (searchResults != null) {
                    updateLocation(searchResults.value!![0].lat, searchResults.value!![0].lon)
                }
            } catch (e: Exception) {
                // Handle error
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }
}