package hu.ait.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import hu.ait.weatherapp.ScrollingActivity.Companion.KEY_WEATHER_ITEM
import hu.ait.weatherapp.data.WeatherResult
import hu.ait.weatherapp.databinding.ActivityWeatherBinding
import hu.ait.weatherapp.retrofit.WeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var city = intent.getStringExtra(KEY_WEATHER_ITEM).toString()
        var retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var weatherAPI = retrofit.create(WeatherAPI::class.java)
        val call = weatherAPI.getWeatherDetails(city,
            "metric",
            getString(R.string.appID)
        )
        call.enqueue(object : Callback<WeatherResult> {
            override fun onResponse(call: Call<WeatherResult>, response: Response<WeatherResult>)
            {
                var weatherResult = response.body()

                Glide.with(this@WeatherActivity)
                    .load(
                        ("https://openweathermap.org/img/w/" +
                                weatherResult?.weather?.get(0)?.icon
                                + ".png"))
                    .into(binding.ivWeather)

                binding.tvName.text = getString(R.string.city_coord, city,
                    weatherResult?.coord?.lat.toString(), weatherResult?.coord?.lon.toString())
                binding.tvDescrip.text = weatherResult?.weather?.get(0)?.description.toString()
                binding.tvHumidity.text = getString(R.string.humidity, weatherResult?.main?.humidity?.toString())
                binding.tvWind.text = getString(R.string.wind, weatherResult?.wind?.speed.toString())
                binding.tvTemp.text = getString(R.string.temp, weatherResult?.main?.temp?.toString(),
                    weatherResult?.main?.feels_like?.toString())
            }
            override fun onFailure(call: Call<WeatherResult>, t: Throwable) {
                Snackbar.make(binding.root, "Could not show weather details for ${city}", Snackbar.LENGTH_LONG).show()
                finish()
            }
        })
    }
}