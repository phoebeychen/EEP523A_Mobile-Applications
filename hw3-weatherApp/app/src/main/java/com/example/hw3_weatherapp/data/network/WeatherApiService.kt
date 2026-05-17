package com.example.hw3_weatherapp.data.network

import com.example.hw3_weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = API_KEY
    ): WeatherResponse

    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = API_KEY
    ): WeatherResponse

    companion object {
        const val API_KEY = "1182dd1c914c6eab8c5a7eb938e16bd5"
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }
}