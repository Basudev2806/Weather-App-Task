package com.basudev.weatherapp.repository

import com.basudev.weatherapp.data.entity.CurrentWeather
import com.basudev.weatherapp.data.entity.Forecast
import com.basudev.weatherapp.data.local.WeatherDatabase
import com.basudev.weatherapp.data.model.ForecastItem
import com.basudev.weatherapp.data.model.LocationSearchResult
import com.basudev.weatherapp.data.remote.WeatherApiService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository(
    private val api: WeatherApiService,
    private val db: WeatherDatabase,
    private val apiKey: String
) {
    suspend fun searchLocations(query: String): List<LocationSearchResult> {
        val response = api.getLocationCoordinates(query, apiKey = apiKey)
        return response.map {
            LocationSearchResult(
                name = it.name,
                country = it.country,
                state = it.state ?: "",
                lat = it.lat,
                lon = it.lon
            )
        }
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather {
        val response = api.getCurrentWeather(lat, lon, apiKey = apiKey)
        return CurrentWeather(
            city = response.name,
            temperature = response.main.temp,
            minTemp = response.main.tempMin,
            maxTemp = response.main.tempMax,
            humidity = response.main.humidity,
            windSpeed = response.wind.speed,
            condition = response.weather.first().main,
            icon = response.weather.first().icon
        )
    }

    suspend fun getFiveDayForecast(lat: Double, lon: Double): List<Forecast> {
        val response = api.getFiveDayForecast(lat, lon, apiKey = apiKey)
        return processForecast(response.list, response.city.name)
    }

    private fun processForecast(items: List<ForecastItem>, city: String): List<Forecast> {
        // Group by day and calculate min/max temps
        val dailyForecasts = items.groupBy {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(it.dt * 1000))
        }

        return dailyForecasts.map { (date, items) ->
            val temps = items.map { it.main.temp }
            Forecast(
                date = date,
                city = city,
                minTemp = temps.minOrNull() ?: 0.0,
                maxTemp = temps.maxOrNull() ?: 0.0,
                condition = items.first().weather.first().main,
                icon = items.first().weather.first().icon
            )
        }
    }
}