package com.basudev.weatherapp

import android.app.Application
import androidx.room.Room
import com.basudev.weatherapp.data.local.WeatherDatabase
import retrofit2.Retrofit

class WeatherApp : Application() {
    val database by lazy { Room.databaseBuilder(this, WeatherDatabase::class.java, "weather-db").build() }
    val retrofit by lazy { Retrofit.Builder()/* ... */.build() }
}