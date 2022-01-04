package hu.ait.weatherapp.retrofit

import hu.ait.weatherapp.data.WeatherResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// link: https://api.openweathermap.org/data/2.5/weather?q=London&appid=be99c9063f017508c7f4eb46b1643a38
// host: https://api.openweathermap.org
// Path: /data/2.5/weather
// Query prams:
// q=London&appid=be99c9063f017508c7f4eb46b1643a38

interface WeatherAPI {
    @GET("data/2.5/weather")
    fun getWeatherDetails(@Query("q") city: String,
                          @Query("units") units: String,
                          @Query("appid") appid: String): Call<WeatherResult>
}