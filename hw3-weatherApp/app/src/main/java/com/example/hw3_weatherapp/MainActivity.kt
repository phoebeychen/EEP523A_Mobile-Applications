package com.example.hw3_weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.hw3_weatherapp.ui.SearchScreen
import com.example.hw3_weatherapp.ui.WeatherHomeContent
import com.example.hw3_weatherapp.ui.WeatherViewModel
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: WeatherViewModel

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getLocation()
        } else {
            // 默认城市
            viewModel.searchWeather("Seattle")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        setContent {
            var showSearch by remember { mutableStateOf(false) }

            // 处理物理返回键
            BackHandler(enabled = showSearch) {
                showSearch = false
            }

            if (showSearch) {
                SearchScreen(
                    viewModel = viewModel,
                    onBack = { showSearch = false },
                    onLocationClick = { 
                        showSearch = false
                        getLocation() 
                    }
                )
            } else {
                WeatherHomeContent(
                    viewModel = viewModel,
                    onSearchClick = { showSearch = true }
                )
            }
        }

        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLocation() {
        val client = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.searchByLocation(location.latitude, location.longitude)
                } else {
                    viewModel.searchWeather("Seattle")
                }
            }
        } else {
            viewModel.searchWeather("Seattle")
        }
    }
}
