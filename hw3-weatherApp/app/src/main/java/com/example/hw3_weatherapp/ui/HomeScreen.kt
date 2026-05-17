package com.example.hw3_weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hw3_weatherapp.data.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherHomeContent(viewModel: WeatherViewModel, onSearchClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF64B5F6), Color(0xFF1E88E5))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 主页面搜索栏：改为点击触发跳转的 Surface
            Surface(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                color = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search city name",
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is WeatherUiState.Loading -> CircularProgressIndicator(color = Color.White)
                is WeatherUiState.Error -> ErrorMessage(state.message)
                is WeatherUiState.Success -> WeatherContent(state.data)
                is WeatherUiState.Empty -> Text("Search for a city to get weather", color = Color.White)
            }
        }
    }
}

@Composable
fun WeatherContent(data: WeatherResponse) {
    // 换算当地日期和时间
    val dateText = remember(data.dt, data.timezone) {
        val sdf = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.format(Date((data.dt + data.timezone) * 1000))
    }

    val sunriseText = remember(data.sys.sunrise, data.timezone) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.format(Date((data.sys.sunrise + data.timezone) * 1000))
    }

    val sunsetText = remember(data.sys.sunset, data.timezone) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.format(Date((data.sys.sunset + data.timezone) * 1000))
    }

    AsyncImage(
        model = "https://openweathermap.org/img/wn/${data.weather[0].icon}@4x.png",
        contentDescription = "weather icon",
        modifier = Modifier.size(120.dp)
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = data.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateText,
                color = Color.White
            )
            Text(
                text = "${data.main.temp.toInt()}°",
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = data.weather[0].main, color = Color.White, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Min", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${data.main.temp_min.toInt()}°C", color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Max", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${data.main.temp_max.toInt()}°C", color = Color.White)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoCard("Sunrise", sunriseText, Modifier.weight(1f))
        InfoCard("Sunset", sunsetText, Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoCard("Wind", "${data.wind.speed} km/h", Modifier.weight(1f))
        InfoCard("Humidity", "${data.main.humidity}%", Modifier.weight(1f))
    }
    
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = Color.White, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White)
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = message,
            color = Color.White,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}
