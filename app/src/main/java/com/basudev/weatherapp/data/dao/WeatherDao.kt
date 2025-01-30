package com.basudev.weatherapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basudev.weatherapp.data.entity.CurrentWeather
import com.basudev.weatherapp.data.entity.Forecast

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrent(weather: CurrentWeather)

    @Query("SELECT * FROM CurrentWeather ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentWeather(): CurrentWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecasts: List<Forecast>)

    @Query("SELECT * FROM Forecast ORDER BY id DESC LIMIT 1")
    suspend fun getForecast(): List<Forecast>
}