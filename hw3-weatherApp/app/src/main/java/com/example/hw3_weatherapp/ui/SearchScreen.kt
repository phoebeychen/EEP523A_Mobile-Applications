package com.example.hw3_weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen(
    viewModel: WeatherViewModel,
    onBack: () -> Unit,
    onLocationClick: () -> Unit
) {
    val recentSearches by viewModel.recentSearches.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun doSearch() {
        if (searchText.isBlank()) {
            errorMessage = "City Name cannot be blank"
        } else {
            errorMessage = null
            viewModel.searchWeather(searchText)
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 搜索栏
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(30.dp))
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF3D4F7C)
                    )
                }
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { 
                        searchText = it
                        if (it.isNotBlank()) errorMessage = null
                    },
                    placeholder = { Text("Search here", color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { doSearch() })
                )
                IconButton(onClick = { doSearch() }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF3D4F7C)
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                )
            }

            // Recent search 标题
            Text(
                text = "Recent search",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D4F7C),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            // 搜索历史列表
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    recentSearches.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.searchWeather(item.cityName)
                                    onBack()
                                }
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF3D4F7C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item.cityName,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3D4F7C),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${item.tempMax}° / ${item.tempMin}°",
                                color = Color(0xFF3D4F7C),
                                fontSize = 14.sp
                            )
                        }
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                }
            }
        }

        // 右下角定位按钮
        FloatingActionButton(
            onClick = onLocationClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "My Location",
                tint = Color(0xFF3D4F7C)
            )
        }
    }
}