package com.example.weathertrackerapp.repository

import com.example.weathertrackerapp.model.LocationData
import com.example.weathertrackerapp.util.LocationRequest

interface UserLocationDetail {
    fun getUserLocation(locationRequest:LocationRequest<LocationData>)
}