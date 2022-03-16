package com.snadinao.weatherapp

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.snadinao.weatherapp.adapter.WeatherAdapter
import com.snadinao.weatherapp.databinding.ActivityMainBinding
import com.snadinao.weatherapp.model.WeatherData
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var CITY = ""
    var API = "b7c7c8bfa88ed59c3c17eebdbd8b0553"
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherList: ArrayList<WeatherData>
    private lateinit var weatherAdapter: WeatherAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obtainLocation()
        onClickWeatherBtn()
//        binding.weatherBtn.setOnClickListener {
//            val cityName = binding.cityName.toString()
//            if (cityName != "") {
//                CITY = cityName
//                WeatherTask().execute()
//            }
//        }
    }

    fun onClickWeatherBtn() {
        val cityName = binding.cityName.toString()
        if (cityName != "") {
            CITY = cityName
            WeatherTask().execute()
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val currentCityName = getCityName(location?.longitude, location?.latitude)
            CITY = currentCityName
            WeatherTask().execute()
        }
    }

    private fun getCityName(longitude: Double?, latitude: Double?): String {
        var cityName = "Not found"
        val gsd = Geocoder(this@MainActivity, Locale.getDefault())
        try {
            val address = gsd.getFromLocation(latitude!!, longitude!!, 10)
            for (adr in address){
                if (adr != null){
                    val city = adr.locality
                    if (city != null && !city.equals("")){
                        cityName = city
                    }
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return cityName
    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask: AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            binding.loader.visibility = View.VISIBLE
            binding.mainContainer.visibility = View.GONE
            binding.errorText.visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            val response: String? = try {
                URL("https://api.openweathermap.org/data/2.5/weather?q=London&units=metric&appid=$API")
                    .readText(Charsets.UTF_8)
            } catch (e: Exception){
                null
            }
            return response
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result!!)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at:" + SimpleDateFormat("dd.MM.yyyy - hh:mm a", Locale.getDefault())
                    .format(Date(updatedAt * 1000))

                val temp = main.getString("temp") + "°C"
                val tempMin = "Min temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max temp: " + main.getString("temp_max") + "°C"

                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise = sys.getLong("sunrise")
                val sunset = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")

                val weatherDescription = weather.getString("description")
                val myAddress = jsonObj.getString("name") + ", " + sys.getString("country")

                binding.address.text = myAddress
                binding.updatedAt.text = updatedAtText
                binding.status.text = weatherDescription.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                binding.temps.text = temp
                binding.tempMin.text = tempMin
                binding.tempMax.text = tempMax

                weatherList = ArrayList()
                weatherList.add(WeatherData(R.drawable.sunrise, "Sunrise",
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(sunrise * 1000))))
                weatherList.add(WeatherData(R.drawable.sunset, "Sunset",
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(sunset * 1000))))
                weatherList.add(WeatherData(R.drawable.wind, "Wind", windSpeed))
                weatherList.add(WeatherData(R.drawable.pressure, "Pressure", pressure))
                weatherList.add(WeatherData(R.drawable.humidity, "Humidity", humidity))

                binding.tempInfoRecycler.layoutManager = LinearLayoutManager(
                    this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                weatherAdapter = WeatherAdapter(this@MainActivity, weatherList)
                binding.tempInfoRecycler.adapter = weatherAdapter

                binding.loader.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE

            } catch (e: Exception){
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }
        }
    }
}


