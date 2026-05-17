# Weather App - Android Homework 3

A modern, responsive Android weather application built with Jetpack Compose. This app allows users to check real-time weather information for any city worldwide or based on their current GPS location.

## 🚀 Features (How the app works)

- **City Search**: Users can type any city name to fetch current weather data, including temperature, weather conditions, humidity, and wind speed.
- **Location-Based Weather**: Integrated Google Play Services Location API to fetch weather for the user's current coordinates via a Floating Action Button.
- **Search History**: Maintains a "Recent Search" list on the search screen, showing the last 5 cities searched with their respective high/low temperatures.
- **Dynamic Local Time**: Automatically calculates and displays the **local date and time** (including sunrise and sunset) for the searched city by applying the timezone offset provided by the OpenWeatherMap API.
- **State-Based Navigation**: High-performance navigation between the Home and Search screens using internal state management to ensure a smooth UI transition and proper back-stack behavior.
- **Robust Error Handling**:
    - Validates empty search inputs ("City Name cannot be blank").
    - Detects invalid city names ("City not found").
    - Identifies network connectivity issues ("Please connect to internet").

## 🛠️ Tech Stack

- **UI**: Jetpack Compose (Material 3)
- **Networking**: Retrofit & Gson
- **Image Loading**: Coil (for weather icons)
- **Architecture**: MVVM with StateFlow
- **API**: OpenWeatherMap API

## 🧠 Challenges Faced

1.  **Timezone Conversion**: One of the biggest challenges was ensuring that sunrise and sunset times reflected the city's local time rather than the user's phone time. I solved this by using the `timezone` offset (seconds from UTC) and formatting the date using `java.util.TimeZone("UTC")` to prevent the system from applying local phone offsets twice.
2.  **Navigation & Focus**: Switching between the Home and Search screens required careful state management. I implemented a clickable `Surface` as a "fake" search bar on the Home screen to trigger the transition, and used `FocusRequester` in a `LaunchedEffect` on the Search screen to ensure the keyboard opens automatically for a better UX.
3.  **Refined Error Handling**: Distinguishing between a 404 error (wrong city name) and a network failure (no internet) required diving into `HttpException` and `IOException` handling within the Coroutine scope of the ViewModel.
4.  **UI Consistency**: Creating a translucent "Glassmorphism" effect using `alpha` on Cards while maintaining readability against a vertical gradient background.

## ⏱️ Number of Hours Worked

Approximately **12-14 hours**.

## 🙏 Acknowledgments

- **OpenWeatherMap API**: For providing the comprehensive weather data.
- **Android Developer Documentation**: For guidance on Jetpack Compose best practices and Location Services.
- **Coil**: For the seamless image loading library.
