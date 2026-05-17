package com.example.hw3_weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3_weatherapp.data.model.WeatherResponse
import com.example.hw3_weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class RecentSearchItem(
    val cityName: String,
    val tempMax: Int,
    val tempMin: Int
)

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
    object Empty : WeatherUiState()
}

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val _recentSearches = MutableStateFlow<List<RecentSearchItem>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearchItem>> = _recentSearches

    fun searchWeather(city: String) {
        if (city.isBlank()) {
            _uiState.value = WeatherUiState.Error("City Name cannot be blank")
            return
        }
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            val result = repository.getWeatherByCity(city)
            result.fold(
                onSuccess = { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                    val newItem = RecentSearchItem(
                        cityName = weather.name,
                        tempMax = weather.main.temp_max.toInt(),
                        tempMin = weather.main.temp_min.toInt()
                    )
                    val updated = (_recentSearches.value
                        .filter { it.cityName != weather.name } + newItem)
                        .takeLast(5)
                    _recentSearches.value = updated
                },
                onFailure = { e ->
                    android.util.Log.e("WeatherViewModel", "Error: ${e.javaClass.name} - ${e.message}")
                    val msg = when {
                        e is HttpException && e.code() == 404 -> "City not found. Please check the name."
                        e is java.io.IOException -> "Please connect to internet"
                        else -> "Something went wrong: ${e.message}"
                    }
                    _uiState.value = WeatherUiState.Error(msg)
                }
            )
        }
    }

    fun searchByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            val result = repository.getWeatherByLocation(lat, lon)
            result.fold(
                onSuccess = { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                },
                onFailure = { e ->
                    android.util.Log.e("WeatherViewModel", "Error: ${e.javaClass.name} - ${e.message}")
                    val msg = when {
                        e is HttpException && e.code() == 404 -> "City not found. Please check the name."
                        e is java.io.IOException -> "Please connect to internet"
                        else -> "Something went wrong: ${e.message}"
                    }
                    _uiState.value = WeatherUiState.Error(msg)
                }
            )
        }
    }
}