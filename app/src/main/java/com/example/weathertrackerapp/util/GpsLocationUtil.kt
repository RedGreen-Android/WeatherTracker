package com.example.weathertrackerapp.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.example.weathertrackerapp.repository.UserLocationImpl
import com.example.weathertrackerapp.util.Constant.GPS_CODE
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient

//A Boiler plate GPS Provider Util class setup for when we ask user for device location. Left all the logs, while debugging
class GpsLocationUtil(private val context: Context) {

    private val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
    private val locationSettingsRequest: LocationSettingsRequest?
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(UserLocationImpl.locationRequest)
        locationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)
    }

    fun activateGPS(OnGpsListener: OnGpsListener?) {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGpsListener?.gpsStatus(true)
        } else {
            if (locationSettingsRequest != null) {
                settingsClient
                    .checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(context as Activity) {
                        //  GPS enabled, callback GPS status using listener
                        OnGpsListener?.gpsStatus(true)
                    }
                    .addOnFailureListener(context) { e ->
                        when ((e as ApiException).statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check result
                                    val resolution = e as ResolvableApiException
                                    resolution.startResolutionForResult(context, GPS_CODE)
                                } catch (sie: IntentSender.SendIntentException) {
                                    Log.i(
                                        ContentValues.TAG, "" +
                                                "Error: Request unable to execute"
                                    )
                                }

                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                val errorMessage =
                                    "Error: Location settings are inadequate, check and fix in Settings."
                                Log.e(ContentValues.TAG, errorMessage)
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }
        }
    }

    interface OnGpsListener {
        fun gpsStatus(isGPSEnable: Boolean)
    }
}