package com.basudev.weatherapp.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.basudev.weatherapp.R

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification()
        startForeground(1, notification)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()
    }

    private fun createNotification(): Notification {
        val channelId = "location_channel"

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Weather Updates")
            .setContentText("Tracking your location for weather updates.")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true) // Keeps the notification persistent
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority to ensure visibility
            .build()
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let {
                        // Send location to ViewModel via LiveData or Broadcast
//                        sendLocationToActivity(it)
                        Toast.makeText(applicationContext, "${it.latitude}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "location_channel"
            val channelName = "Location Updates"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for location updates"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    private fun sendLocationToActivity(location: Location) {
        val intent = Intent("LOCATION_UPDATE").apply {
            putExtra("lat", location.latitude)
            putExtra("lon", location.longitude)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}