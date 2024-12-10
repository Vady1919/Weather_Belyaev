package com.example.weather


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import android.widget.Toast
import com.bumptech.glide.Glide
import com.squareup.moshi.JsonDataException

class MainActivity : AppCompatActivity() {

    private lateinit var locationEditText: EditText
    private lateinit var fetchButton: Button
    private lateinit var weatherTextView: TextView
    private lateinit var weatherIconImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationEditText = findViewById(R.id.locationEditText)
        fetchButton = findViewById(R.id.fetchButton)
        weatherTextView = findViewById(R.id.weatherTextView)
        weatherIconImageView = findViewById(R.id.weatherIconImageView)

        fetchButton.setOnClickListener {
            val location = locationEditText.text.toString()
            if (location.isNotEmpty()) {
                fetchWeatherData(location)
            } else {
                Toast.makeText(this, "Введите название города", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun fetchWeatherData(location: String) {
        val apiKey = "eb052046e84fb028b18d94af9677ada6"
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$location&appid=$apiKey&units=metric&lang=ru"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Произошла ошибка сети", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                if (response.isSuccessful && json != null) {
                    val weatherResponse = parseWeatherResponse(json)
                    runOnUiThread {
                        updateWeatherUI(weatherResponse)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Ошибка: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
    private fun parseWeatherResponse(json: String): Weather {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()


        // Создаём адаптер для объекта OpenWeatherResponse
        val jsonAdapter = moshi.adapter(OpenWeatherResponse::class.java)


        return try {
            val response = jsonAdapter.fromJson(json)

            // Преобразуем в объект Weather
            Weather(
                temperature = response?.main?.temp ?: 0.0, // Используем Double
                humidity = response?.main?.humidity?.toDouble() ?: 0.0, // Преобразуем к Double
                description = response?.weather?.firstOrNull()?.description ?: "Нет данных"

            )
        } catch (e: JsonDataException) {
            e.printStackTrace()
            Weather(
                temperature = 0.0, // Используем Double
                humidity = 0.0, // Используем Double
                description = "Ошибка обработки данных"

            )
        }
    }


    private fun updateWeatherUI(weather: Weather) {
        // Текст для описания погоды
        val weatherText = """
        Температура: ${weather.temperature}°C
        Влажность: ${weather.humidity}%
        Описание: ${weather.description}
    """.trimIndent()

        weatherTextView.text = weatherText
    }
}
