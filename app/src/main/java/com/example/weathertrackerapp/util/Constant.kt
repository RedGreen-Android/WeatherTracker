package com.example.weathertrackerapp.util

object Constant {
    //**I am aware for best practices and security purposes, I should put the API_KEY in
    //gradle.properties and set variables in build.gradle to be used by BuildConfig upon code generation (also api key is in git ignore and hidden from git commits). However, due to lack of time I was unable to do it but will on proceeding commit to git
    const val API_KEY = "ea42a2c21d4b2d9eb78ac89a1da37bd9"

    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val API_ENDPOINT = "weather"

    const val LOCATION_KEY = "LOCATION"
    const val DEFAULT_LOCATION = "Atlanta"
    const val PREF_KEY = "WeatherPref"

    const val GPS_CODE = 200
    const val REQUEST_LOCATION = 100

    const val IMAGE_BASE_URL = "https://openweathermap.org/img/wn/"
    const val IMAGE_END = "@2x.png"

    const val IS_SEARCHED = "Searched"
}