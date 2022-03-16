package com.snadinao.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.snadinao.weatherapp.R
import com.snadinao.weatherapp.model.WeatherData

class WeatherAdapter(val context: Context, var weatherList: ArrayList<WeatherData>):
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.weather_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val newWeatherList = weatherList[position]
        holder.temps.text = newWeatherList.temp
        holder.tempInfo.text = newWeatherList.tempInfo
        holder.tempImg.setImageResource(newWeatherList.img)
    }

    override fun getItemCount(): Int{
        return weatherList.size
    }

    inner class WeatherViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val tempImg: ImageView = view.findViewById(R.id.tempImage)
        val temps: TextView = view.findViewById(R.id.temps)
        val tempInfo: TextView = view.findViewById(R.id.tempsInfo)

    }
}