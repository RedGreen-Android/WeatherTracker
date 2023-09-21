package com.example.weathertrackerapp.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.weathertrackerapp.util.Constant.DEFAULT_LOCATION
import com.example.weathertrackerapp.util.Constant.IS_SEARCHED
import com.example.weathertrackerapp.util.Constant.LOCATION_KEY
import com.example.weathertrackerapp.util.Constant.PREF_KEY
import javax.inject.Inject

class SharedPreference @Inject constructor(private val context: Context){

    private val preferences: SharedPreferences
        get() =  context.getSharedPreferences(PREF_KEY,Context.MODE_PRIVATE)

    fun setLocation(city: String) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(LOCATION_KEY, city).apply()
    }

    fun getLocation(): String? {
        return preferences.getString(LOCATION_KEY, DEFAULT_LOCATION)
    }

    fun isSearched(): Boolean {
        return preferences.getBoolean(IS_SEARCHED,false )
    }

    fun setSearched(isClicked: Boolean) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean(IS_SEARCHED, isClicked).apply()
    }
}