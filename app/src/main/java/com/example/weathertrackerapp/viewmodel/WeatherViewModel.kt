package com.example.weathertrackerapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertrackerapp.model.IconData
import com.example.weathertrackerapp.model.LocationData
import com.example.weathertrackerapp.model.WeatherResponse
import com.example.weathertrackerapp.repository.UserLocationImpl
import com.example.weathertrackerapp.repository.WeatherRepository
import com.example.weathertrackerapp.util.Constant.IMAGE_BASE_URL
import com.example.weathertrackerapp.util.Constant.IMAGE_END
import com.example.weathertrackerapp.util.LocationRequest
import com.example.weathertrackerapp.util.Resource
import com.example.weathertrackerapp.util.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    val sharedPreference: SharedPreference,
    private val userLocation: UserLocationImpl,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context // Use @ApplicationContext here
) : ViewModel() {

    private val _weatherData: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    val weatherData: LiveData<Resource<WeatherResponse>> = _weatherData

    private val _weatherUserLocation: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    val weatherUserLocation: LiveData<Resource<WeatherResponse>> = _weatherUserLocation

    private val _iconLiveData: MutableLiveData<IconData> = MutableLiveData()
    val iconLiveData: LiveData<IconData> = _iconLiveData  //Live data for icon using data binding - unused

    private val _locationLiveData: MutableLiveData<LocationData> = MutableLiveData()
    val locationLiveData: LiveData<LocationData> = _locationLiveData

    private val _locationFailureLiveData = MutableLiveData<String?>()

    private var weatherResponse: WeatherResponse? = null

    private fun getWeatherResponse(response: Response<WeatherResponse>): Resource<WeatherResponse>? {
        return if (response.isSuccessful) Resource.success(response.body())
        else Resource.error(data = null, "Error: ${response.errorBody()}")
    }

    fun getWeatherData() {
        viewModelScope.launch {
            _weatherData.postValue(Resource.loading(null))
            try {
                // Load the last city searched by user from SharedPreferences
                val response = sharedPreference.getLocation()?.let { repository.getWeather(it) }
                _weatherData.postValue(response?.let { getWeatherResponse(it) })
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _weatherData.postValue(
                        Resource.error(
                            data = null,
                            message = "Error: Network Failure"
                        )
                    )
                    else -> _weatherData.postValue(
                        Resource.error(
                            data = null,
                            message = t.message ?: "Error: "
                        )
                    )
                }
            }
        }
    }

    fun getWeatherByLocation(lat: String, lon: String) {
        viewModelScope.launch {
            _weatherUserLocation.postValue(Resource.loading(null))
            try {
                val response = repository.getWeatherByLocation(lat, lon)
                _weatherUserLocation.postValue(getWeatherResponse(response))
                Log.d("MyApp", "Weather data fetched: $weatherData")
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _weatherUserLocation.postValue(
                        Resource.error(
                            null,
                            "Network Failure"
                        )
                    )
                    else -> _weatherUserLocation.postValue(Resource.error(null, t.message))
                }
            }
        }
    }

    //The approached I used to try to have "Data binding" with @bindingadapter for the icon, due to time constrains decided to stick to glide
    fun getIconImage() {
        viewModelScope.launch {
            val response =
                repository.getIcon(IMAGE_BASE_URL + weatherResponse?.weather?.getOrNull(0)?.icon + IMAGE_END)
            _iconLiveData.postValue(response)
        }
    }

    // Use a geocoding service to get the latitude and longitude for the city
    fun getLatLonForCity(city: String): Pair<String, String> {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(city, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val latitude = addresses[0].latitude.toString()
                val longitude = addresses[0].longitude.toString()
                return Pair(latitude, longitude)
            }
        }
        // Default to Atlanta coordinates if the city is not found
        return Pair("33.749", "-84.388")
    }


    /***
     * Given more time, I would have a Domain layer with user cases and UI states as
     * Clean architecture would future assist the separation of concern with the repository patter
     */

    fun getUserLocation() {
        userLocation.getUserLocation(object : LocationRequest<LocationData> {
            override fun onSuccess(data: LocationData) {
                _locationLiveData.postValue(data)
            }

            override fun onFailed(errorMessage: String?) {
                _locationFailureLiveData.postValue(errorMessage)
            }

        })
    }
}