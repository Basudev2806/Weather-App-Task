package com.basudev.weatherapp.viewmodel

import android.content.Context
import android.net.ConnectivityManager
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

    fun updateLocation(context: Context, lat: Double, lon: Double) {
        _location.value = Pair(lat, lon)
        loadWeatherData(context)
    }

    private fun loadWeatherData(context: Context) {
        _location.value?.let { (lat, lon) ->
            viewModelScope.launch {
                try {
                    if (isNetworkAvailable(context)) {
                        // Fetch data from API
                        _currentWeather.value = repository.getCurrentWeather(lat, lon)
                        _forecast.value = repository.getFiveDayForecast(lat, lon)
                    } else {
                        // Fetch data from local database
                        fetchWeatherDataFromDb()
                    }

                } catch (e: Exception) {
                    // Handle error
                    _errorMessage.value = "Something went wrong. Please try again."
                }
            }
        }
    }

    private suspend fun fetchWeatherDataFromDb() {

        if (repository.getForecastDataFromDb() != null && repository.getWeatherDataFromDb() != null) {
            // Update LiveData with the last inserted data
            _currentWeather.value = repository.getWeatherDataFromDb()
            _forecast.value = repository.getForecastDataFromDb() // Assuming forecast is a list
        } else {

        }
    }


    private val _searchResults = MutableLiveData<List<LocationSearchResult>>()
    val searchResults: LiveData<List<LocationSearchResult>> = _searchResults

    fun searchLocations(context: Context ,query: String) {
        viewModelScope.launch {
            try {
                _searchResults.value = repository.searchLocations(query)
                if (searchResults != null) {
                    updateLocation(context, searchResults.value!![0].lat, searchResults.value!![0].lon)
                }
            } catch (e: Exception) {
                // Handle error
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        // Check network availability
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}