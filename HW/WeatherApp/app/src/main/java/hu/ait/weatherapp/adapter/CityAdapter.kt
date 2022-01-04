package hu.ait.weatherapp.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.ait.weatherapp.ScrollingActivity
import hu.ait.weatherapp.ScrollingActivity.Companion.KEY_WEATHER_ITEM
import hu.ait.weatherapp.WeatherActivity
import hu.ait.weatherapp.databinding.CityRowBinding

class CityAdapter: RecyclerView.Adapter<CityAdapter.ViewHolder> {
    val context: Context

    // items should come from database
    var cities = mutableListOf<String>()

    constructor(context: Context): super(){
        this.context = context
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    // creates new item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cityRowBinding = CityRowBinding.inflate(
            LayoutInflater.from(context),
            parent, false)
        return ViewHolder(cityRowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCity = cities[position]
        holder.bind(currentCity)

        holder.cityRowBinding.cardView.setOnClickListener{
            val weatherIntent = Intent((context as ScrollingActivity), WeatherActivity::class.java)
            weatherIntent.putExtra(KEY_WEATHER_ITEM, currentCity)
            (context as ScrollingActivity).startActivity(weatherIntent)
        }

        holder.cityRowBinding.btnDel.setOnClickListener{
            deleteCity(holder.adapterPosition)
        }
    }

    fun addCity(newCity: String){
        cities.add(newCity)
        notifyItemChanged(cities.lastIndex)
    }

    fun deleteCity(index: Int) {
        cities.removeAt(index)
        //notifyDataSetChanged()
        notifyItemRemoved(index)
    }

    inner class ViewHolder(val cityRowBinding: CityRowBinding) : RecyclerView.ViewHolder(cityRowBinding.root) {
        fun bind(city: String) {
            cityRowBinding.tvCity.text = city
        }
    }
}