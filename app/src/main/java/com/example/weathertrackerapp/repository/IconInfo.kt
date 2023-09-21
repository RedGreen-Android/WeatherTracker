package com.example.weathertrackerapp.repository

import com.example.weathertrackerapp.model.IconData

class IconInfo {
    //mapping the IconData to repository and designing it to have it invoked in viewmodel
    suspend fun getIcon (weatherResponse: String) : IconData = IconData()
}