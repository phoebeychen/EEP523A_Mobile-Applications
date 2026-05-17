package com.example.hw3_weatherapp.data.repository

import com.example.hw3_weatherapp.data.model.WeatherResponse
import com.example.hw3_weatherapp.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {
    private val api = RetrofitInstance.api

    suspend fun getWeatherByCity(city: String): Result<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(api.getWeatherByCity(city))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(api.getWeatherByLocation(lat, lon))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}