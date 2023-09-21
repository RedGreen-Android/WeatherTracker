package com.example.weathertrackerapp.network

import com.example.weathertrackerapp.model.WeatherResponse
import com.example.weathertrackerapp.util.Constant
import com.example.weathertrackerapp.util.Constant.API_ENDPOINT
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    //sample api w/t endpoints url --> https://api.openweathermap.org/data/2.5/weather?q=Dallas,US&appid=ea42a2c21d4b2d9eb78ac89a1da37bd9

    @GET(API_ENDPOINT)
    suspend fun getWeather(
        @Query("q") location:String = "Dallas",
        @Query("appid") apiKey: String = Constant.API_KEY
    ): Response<WeatherResponse>

    @GET(API_ENDPOINT)
    suspend fun getWeatherByLocation(
        @Query("lat")
        latitude:String,
        @Query("lon")
        longitude:String,
        @Query("appid") apiKey: String = Constant.API_KEY
    ):Response<WeatherResponse>
}