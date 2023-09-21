package com.example.weathertrackerapp.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.example.weathertrackerapp.model.LocationData
import com.example.weathertrackerapp.util.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import javax.inject.Inject


class UserLocationImpl @Inject constructor(private val context: Context) : UserLocationDetail {

    private var clientLocation = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getUserLocation(locationRequest: LocationRequest<LocationData>) {
        try {
            clientLocation.lastLocation.addOnSuccessListener {
                it.also { locationRequest.onSuccess(setLocationData(it)) }
            }.addOnFailureListener {
                locationRequest.onFailed(it.message)
            }
            startLocationUpdates()
        } catch (e: Exception) {
            Log.e("MyApp", "Error getting user location: ${e.message}")
            locationRequest.onFailed(e.message)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations) {
                Log.d("MyApp", "New Location: ${location.latitude}, ${location.longitude}")
                setLocationData(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        clientLocation.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun setLocationData(location: Location): LocationData {
        return LocationData(longitude = location.longitude, latitude = location.latitude)
    }

    companion object {
        val locationRequest: com.google.android.gms.location.LocationRequest =
            com.google.android.gms.location.LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            }
    }
}