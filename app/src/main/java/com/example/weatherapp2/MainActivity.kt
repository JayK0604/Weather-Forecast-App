@file:Suppress("DEPRECATION")

package com.example.weatherapp2

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DUPLICATE_LABEL_IN_WHEN")
class MainActivity : AppCompatActivity() {

    val api: String = "99ac3c79880072b6e7ea71f510594ed9"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.searchButton).setOnClickListener {
            val location = findViewById<EditText>(R.id.locationInput).text.toString()
            if (location.isNotEmpty()) {
                WeatherTask(location).execute()
            } else {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask(private val city: String) : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.mainContainer).visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): String? {
            return try {
                URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$api")
                    .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result.toString())
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
                val temp = main.getString("temp") + "°C"
                val pressure = main.getString("pressure") + " hPa"
                val humidity = main.getString("humidity") + " %"
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + " m/s"
                val seaLevel = main.optString("sea_level", "No Water Nearby") + " msl"
                val weatherDescription = weather.getString("description").toLowerCase(Locale.ROOT)
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize(Locale.ROOT)
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<TextView>(R.id.sea_level).text = seaLevel

                // Log the weather description
                Log.d("WeatherApp", "Weather description: $weatherDescription")

                // Set the appropriate animation based on the weather description
                val animationView = findViewById<LottieAnimationView>(R.id.animationView)
                val mainLayout = findViewById<ConstraintLayout>(R.id.mainLayout)

                when (weatherDescription) {
                    "clear sky", "sunny" -> {
                        animationView.setAnimation(R.raw.clear_sky)
                        mainLayout.setBackgroundResource(R.drawable.sunny_bg)
                    }
                    "few clouds", "scattered clouds", "broken clouds", "overcast clouds" -> {
                        animationView.setAnimation(R.raw.cloudy)
                        mainLayout.setBackgroundResource(R.drawable.cloudy_bg)
                    }
                    "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain" -> {
                        animationView.setAnimation(R.raw.rain)
                        mainLayout.setBackgroundResource(R.drawable.rain_bg)
                    }
                    "thunderstorm", "thunderstorm with light rain", "thunderstorm with rain", "thunderstorm with heavy rain", "light thunderstorm", "heavy thunderstorm", "ragged thunderstorm", "thunderstorm with light drizzle", "thunderstorm with drizzle", "thunderstorm with heavy drizzle" -> {
                        animationView.setAnimation(R.raw.thunderstorm)
                        mainLayout.setBackgroundResource(R.drawable.thunderstorm_bg)
                    }
                    "snow", "light snow", "heavy snow", "sleet", "light shower sleet", "shower sleet", "light rain and snow", "rain and snow", "light shower snow", "shower snow", "heavy shower snow" -> {
                        animationView.setAnimation(R.raw.snow)
                        mainLayout.setBackgroundResource(R.drawable.snow_bg)
                    }
                    "mist", "smoke", "haze", "sand/ dust whirls", "fog", "sand", "dust", "volcanic ash", "squalls", "tornado" -> {
                        animationView.setAnimation(R.raw.mist)
                        mainLayout.setBackgroundResource(R.drawable.mist_bg)
                    }
                    "drizzle", "light intensity drizzle", "drizzle rain", "heavy intensity drizzle", "light intensity drizzle rain", "drizzle rain", "heavy intensity drizzle rain", "shower drizzle", "heavy shower drizzle" -> {
                        animationView.setAnimation(R.raw.drizzle)
                        mainLayout.setBackgroundResource(R.drawable.drizzle_bg)
                    }
                    else -> {
                        animationView.setAnimation(R.raw.default_weather)
                        mainLayout.setBackgroundResource(R.drawable.default_bg)
                    }
                }

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<LinearLayout>(R.id.mainContainer).visibility = View.VISIBLE
            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                Toast.makeText(this@MainActivity, "Location not found", Toast.LENGTH_LONG).show()
            }
        }
    }
}
