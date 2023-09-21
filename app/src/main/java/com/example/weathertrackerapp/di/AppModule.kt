package com.example.weathertrackerapp.di

import android.content.Context
import com.example.weathertrackerapp.repository.IconInfo
import com.example.weathertrackerapp.repository.UserLocationImpl
import com.example.weathertrackerapp.util.LocationRequest
import com.example.weathertrackerapp.util.SharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //As these classes are dependencies in multiple places, having same instances save memory allocation.
    //Injecting them whenever needed to save the creating this object multiple times which is expensive, ease of testing.
    @Singleton
    @Provides
    fun providePreference(
        @ApplicationContext context: Context
    ) = SharedPreference(context)

    @Singleton
    @Provides
    fun provideUserLocation(
        @ApplicationContext context: Context
    ) = UserLocationImpl(context)

    @Singleton
    @Provides
    fun provideIconInfo() = IconInfo()
}