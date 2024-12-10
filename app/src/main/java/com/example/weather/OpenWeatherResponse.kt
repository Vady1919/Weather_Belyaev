package com.example.weather

data class OpenWeatherResponse(
    val main: Main,
    val weather: List<WeatherDescription>
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class WeatherDescription(
    val description: String,
    val icon: String
)
