package com.example.weather

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.JsonDataException

data class Weather(
    val temperature: Double, // Изменено с Float на Double
    val humidity: Double,    // Изменено с Float на Double
    val description: String,
) {
    companion object {
        private fun translateWeatherDescription(description: String): String {
            return when (description) {
                "clear sky" -> "ясное небо"
                "few clouds" -> "немного облаков"
                "scattered clouds" -> "рассеянные облака"
                "broken clouds" -> "разорванные облака"
                "shower rain" -> "ливневый дождь"
                "rain" -> "дождь"
                "thunderstorm" -> "гроза"
                "snow" -> "снег"
                "mist" -> "туман"
                "overcast clouds" -> "пасмурные облака"
                else -> description // Оставить оригинал, если перевода нет
            }
        }

        fun fromJson(json: String): Weather {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter = moshi.adapter(OpenWeatherResponse::class.java)

            return try {
                val response = jsonAdapter.fromJson(json)

                Weather(
                    temperature = response?.main?.temp ?: 0.0,
                    humidity = response?.main?.humidity?.toDouble() ?: 0.0,
                    description = translateWeatherDescription(response?.weather?.firstOrNull()?.description ?: "Нет данных")
                )
            } catch (e: JsonDataException) {
                e.printStackTrace()
                Weather(
                    temperature = 0.0,
                    humidity = 0.0,
                    description = "Ошибка обработки данных"

                )
            }
        }

    }
}
