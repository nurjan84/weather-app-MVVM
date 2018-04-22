package kz.nta.weather.mvvm.views

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kz.nta.weather.R
import kz.nta.weather.adapters.WeatherListAdapter
import kz.nta.weather.databinding.ActivityMainBinding
import kz.nta.weather.mvvm.viewModels.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel:WeatherViewModel
    private lateinit var adapter : WeatherListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        weatherViewModel.initViewModel(getString(R.string.places_api_key),getString(R.string.weather_api_key))

        val binding:ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.weatherViewModel = weatherViewModel

        weatherListener()
    }

    private fun weatherListener(){
        weatherViewModel.getWeather().observe(this, Observer{ resultList ->
            if (::adapter.isInitialized){
                adapter.updateList(resultList!!)
            }else{
                adapter = WeatherListAdapter(this,resultList!!)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter
            }
        })
    }



}
