package com.example.weathertrackerapp.repository

import com.example.weathertrackerapp.network.WeatherAPI
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val api: WeatherAPI, private val iconInfo: IconInfo) {

    suspend fun getWeather(id:String) = api.getWeather(id,  country = "US")
    suspend fun getWeatherByLocation(lat:String,lon:String) = api.getWeatherByLocation(lat,lon)
    suspend fun getIcon(weatherResponse: String) = iconInfo.getIcon(weatherResponse)

}