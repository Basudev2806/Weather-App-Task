package com.basudev.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("name") val cityName: String
)

// Current Weather Response
data class CurrentWeatherResponse(
    @SerializedName("coord") val coord: Coordinates,
    @SerializedName("main") val main: Main,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("name") val name: String
)

// Forecast Response
data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: City
)

// Geocoding Response
data class GeocodingResponse(
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String?
)

// Common models
data class Coordinates(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("humidity") val humidity: Int
)

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int
)

data class Weather(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class ForecastItem(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("dt_txt") val dtTxt: String
)

data class City(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String
)