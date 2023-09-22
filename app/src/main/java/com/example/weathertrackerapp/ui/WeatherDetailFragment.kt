package com.example.weathertrackerapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.weathertrackerapp.R
import com.example.weathertrackerapp.databinding.FragmentWeatherDetailBinding
import com.example.weathertrackerapp.util.Constant
import com.example.weathertrackerapp.util.Constant.DEFAULT_LOCATION
import com.example.weathertrackerapp.util.Constant.REQUEST_LOCATION
import com.example.weathertrackerapp.util.GpsLocationUtil
import com.example.weathertrackerapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Screen to display the weather information fetched from api, using mvvm with repository pattern and
 * modular code. If given more time, I would use "Clean Architecture" with Use Cases (domain and data layers)
 * to further enhance the separation of concern. Needless to say, would have used Jetpack Compose
 */
@AndroidEntryPoint
class WeatherDetailFragment : Fragment(R.layout.fragment_weather_detail) {

    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var binding: FragmentWeatherDetailBinding

    private var isGPSEnabled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherDetailBinding.bind(view)
        checkGPS()
        setupUI()
        weatherObserver()
    }

    override fun onStart() {
        super.onStart()
        checkAndRequestLocation()
    }

    private fun checkGPS(){
        //checking GPS status
        GpsLocationUtil(requireContext()).activateGPS(object : GpsLocationUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                isGPSEnabled = isGPSEnable
            }
        })
    }

    private fun setupUI() {
        //upon click on search icon, populate city User typed, (putting logic in a function would make code more abstract)
        binding.ivSearchCity.setOnClickListener {
            var cityName =
                binding.etCityName.text.toString().trim() // Remove leading/trailing spaces
            // **Ensure that user does not click on search without typing a city, handling edge cases
            if (binding.etCityName.text.toString().isEmpty()) {
                Toast.makeText(activity, "Error: Please Enter a City", Toast.LENGTH_SHORT).show();
            } else if (!cityName.matches(Regex("^[a-zA-Z\\s,]*\$"))) {
                // *Check if cityName contains only letters and spaces in between the word, ex:New York
                Toast.makeText(
                    activity,
                    "Error: Please Enter a valid US City Name",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //Hack to make sure ONLY "US" cities (US Country code) is shown,
                //as Rest Api Query is "q={city name},{country code} ex: Atlanta,US
                val cityUS = if (cityName.endsWith(",US")) {
                    cityName
                } else {
                    "$cityName,US"
                }
                weatherViewModel.sharedPreference.setLocation(cityUS)
                weatherViewModel.sharedPreference.setSearched(true)
                weatherViewModel.getWeatherData()
            }
        }
    }

    private fun weatherObserver() {
        /**
         * Hack alert - Due to my thought process that after initial launch of app,
         * User must search for a city, if so, thus clicking on "binding.ivSearchCity" logo widget. Hence in order me to save the last searched city location,
         * I must use a flag to indicate that user has searched/clicked on the search logo. Therefore, if sharedPref boolean is set to true after first onClick, as permission has been granted,
         * it indicates that instead of device default location in the next launch of app, rather save the last city typed by user after re-lauching app proceeding times.
         * Also, if they did not click on search logo, that means the next launch we can have the user default Device location populate as that is the first/last location. Considering time limitation, works well in all edge cases tested.
         */
        if (weatherViewModel.sharedPreference.isSearched()) {
            // Location data is available, use it to fetch weather data
            val lastSearchedCity: String = weatherViewModel.sharedPreference.getLocation() ?: DEFAULT_LOCATION
            val (latitude, longitude) = weatherViewModel.getLatLonForCity(lastSearchedCity)
            weatherViewModel.getWeatherByLocation(latitude, longitude)
        } else {
            // Location data is not available, use the last searched city
            weatherViewModel.locationLiveData.observe(viewLifecycleOwner) {
                weatherViewModel.getWeatherByLocation(
                    it.latitude.toString(),
                    it.longitude.toString()
                )
            }
        }

        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weather ->
            binding.weather = weather.data
            context?.let {
                Glide.with(it)
                    .load(Constant.IMAGE_BASE_URL + (weather.data?.weather?.get(0)?.icon) + Constant.IMAGE_END)
                    .into(binding.ivIcon)
            }
        }

        weatherViewModel.weatherUserLocation.observe(viewLifecycleOwner) { location ->
            binding.weather = location.data
            //here the icon is fetched as location is available as per launch of app
            context?.let {
                Glide.with(it)
                    .load(Constant.IMAGE_BASE_URL + (location.data?.weather?.get(0)?.icon) + Constant.IMAGE_END)
                    .into(binding.ivIcon)
            }
        }
    }

    private fun checkAndRequestLocation() {
        Log.d("MyApp", "checkAndRequestLocation: isGPSEnabled=$isGPSEnabled")
        when {
            !isGPSEnabled -> Toast.makeText(activity, "Error: GPS Not Enabled", Toast.LENGTH_SHORT)
                .show();
            isPermissionsGranted() -> startLocationUpdate()
            shouldShowRequestPermissionRationale() -> requestLocationPermission()
            else -> requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION
        )
    }

    private fun startLocationUpdate() {
        weatherViewModel.getUserLocation()
    }

    private fun isPermissionsGranted(): Boolean {
        val context = requireContext()
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun shouldShowRequestPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if (isPermissionsGranted()) {
                // Location permission granted, start location updates
                startLocationUpdate()
            } else {
                // Location permission denied, handle it here
                Toast.makeText(
                    requireContext(),
                    "Location permission denied. Please enable it in settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}