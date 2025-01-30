package com.basudev.weatherapp.data.remote

import com.basudev.weatherapp.data.model.CurrentWeatherResponse
import com.basudev.weatherapp.data.model.ForecastResponse
import com.basudev.weatherapp.data.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    // Current Weather
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): CurrentWeatherResponse

    // 5-Day Forecast
    @GET("data/2.5/forecast")
    suspend fun getFiveDayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): ForecastResponse

    // Geocoding API for search
    @GET("geo/1.0/direct")
    suspend fun getLocationCoordinates(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<GeocodingResponse>
}