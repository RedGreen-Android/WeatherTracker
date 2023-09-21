package com.example.weathertrackerapp.util

interface LocationRequest<T> {
    fun onSuccess(data:T)
    fun onFailed(errorMessage:String?)
}