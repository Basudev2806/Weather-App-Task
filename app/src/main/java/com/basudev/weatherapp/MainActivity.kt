package com.basudev.weatherapp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.basudev.weatherapp.adapters.ForecastAdapter
import com.basudev.weatherapp.data.local.WeatherDatabase
import com.basudev.weatherapp.data.remote.WeatherApiService
import com.basudev.weatherapp.repository.WeatherRepository
import com.basudev.weatherapp.services.LocationService
import com.basudev.weatherapp.viewmodel.WeatherViewModel
import com.basudev.weatherapp.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var forecastAdapter: ForecastAdapter

    // Views
    private lateinit var tvCity: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var tvMinMaxTemp: TextView
    private lateinit var ivCurrentIcon: ImageView
    private lateinit var rvForecast: RecyclerView
    private lateinit var searchView: SearchView

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startLocationService()
        } else {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        initViews()
        setupRecyclerView()

        setupSearchView()
        checkLocationPermission()

        // Initialize repository
        val apiService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)

        val database = WeatherDatabase.getDatabase(applicationContext)
        val repository = WeatherRepository(apiService, database, "0e518abd979f6cba3d51b1a6fb655081")

        viewModel = ViewModelProvider(this, WeatherViewModelFactory(repository))[WeatherViewModel::class.java]
        setupObservers()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        tvCity = findViewById(R.id.tvCity)
        tvTemperature = findViewById(R.id.tvCurrentTemp)
        tvCondition = findViewById(R.id.tvCondition)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvWindSpeed = findViewById(R.id.tvWindSpeed)
        tvMinMaxTemp = findViewById(R.id.tvMinMaxTemp)
        ivCurrentIcon = findViewById(R.id.ivCurrentIcon)
        rvForecast = findViewById(R.id.rvForecast)
        searchView = findViewById(R.id.searchView)
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter()
        rvForecast.apply {
            adapter = forecastAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewModel.currentWeather.observe(this) { weather ->
            weather?.let {
                tvCity.text = it.city
                tvTemperature.text = getString(R.string.temperature_format, it.temperature)
                tvCondition.text = it.condition
                tvHumidity.text = getString(R.string.humidity_format, it.humidity)
                tvWindSpeed.text = getString(R.string.wind_speed_format, it.windSpeed)
                Toast.makeText(this, "${it.maxTemp} ${it.minTemp}", Toast.LENGTH_SHORT).show()
                tvMinMaxTemp.text = getString(R.string.min_max_temp_format, it.maxTemp, it.minTemp)

                Glide.with(this)
                    .load(it.icon)
                    .placeholder(R.drawable.ic_weather_loading)
                    .into(ivCurrentIcon)
            }
        }

        viewModel.forecast.observe(this) { forecasts ->
            forecastAdapter.submitList(forecasts)
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotBlank()) {
                    viewModel.searchLocations(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length > 5) {
                    viewModel.searchLocations(newText)
                }
                return true
            }
        })
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED -> {
                startLocationService()
                getLocation()
            }

            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                Toast.makeText(
                    this,
                    "Location permission is required for accurate weather information",
                    Toast.LENGTH_LONG
                ).show()
                locationPermissionRequest.launch(ACCESS_FINE_LOCATION)
            }

            else -> {
                locationPermissionRequest.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun startLocationService() {
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        ) {
            val serviceIntent = Intent(this, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(locationReceiver, IntentFilter("LOCATION_UPDATE"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(locationReceiver)
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val lat = intent.getDoubleExtra("lat", 0.0)
            val lon = intent.getDoubleExtra("lon", 0.0)
            if (lat != 0.0 && lon != 0.0) {
                viewModel.updateLocation(lat, lon)
            }
        }
    }

    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // Send the location to ViewModel or use directly
                    viewModel.updateLocation(it.latitude, it.longitude)
                }
            }
        }
    }
}