package com.example.weathertrackerapp.di

import com.example.weathertrackerapp.network.WeatherAPI
import com.example.weathertrackerapp.repository.IconInfo
import com.example.weathertrackerapp.util.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideBaseUrl() = Constant.BASE_URL

    @Provides
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun provideOkHttpClient(logger: HttpLoggingInterceptor): OkHttpClient{
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(logger)
        val okHttp = okHttpClient.build()
        return okHttp
    }
    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String,okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): WeatherAPI =
        retrofit.create(WeatherAPI::class.java)

}