package kz.nta.weather.mvvm.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import kz.nta.weather.WeatherApplication
import kz.nta.weather.api.WeatherService
import kz.nta.weather.dagger.components.DaggerServiceComponent
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import android.databinding.ObservableField
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import kz.nta.weather.api.CacheProviders
import kz.nta.weather.mvvm.models.WeatherModel


class WeatherViewModel: ViewModel(){
    init {
        DaggerServiceComponent.builder()
                .appComponent(WeatherApplication.getApplicationComponent())
                .build().injectWeatherViewModel(this)
    }

    @Inject lateinit var apiService: WeatherService
    @Inject lateinit var prefs: SharedPreferences
    @Inject lateinit var cache: CacheProviders

    private val disposables = CompositeDisposable()
    private lateinit var placesKey:String
    private lateinit var weatherKey:String
    private val type = "(cities)"
    var isShowLoader = ObservableField<Boolean>()
    var lastInput = ""
    private val resultWeather =  MutableLiveData<ArrayList<WeatherModel>>()


    fun initViewModel(placesKey:String, weatherKey:String){
        this.placesKey = placesKey
        this.weatherKey = weatherKey
        isShowLoader.set(false)
        getLastInput()
    }

    private fun saveLastRequest(input:String){
        prefs.edit().putString("last_input", input).apply()
    }

    private fun getLastInput(){
        lastInput =  prefs.getString("last_input", "")
        runRequest(lastInput)
    }


    val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            runRequest(s.toString())
        }
    }

    private fun runRequest(s:String){
        disposables.add(
                Observable.just(s)
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .filter { it.length>=2 }
                        .doOnNext {
                            isShowLoader.set(true)
                            saveLastRequest(it)
                        }
                        .switchMap { it ->
                            cache.getCities(apiService.getCities(it, type, placesKey), DynamicKey(it))
                                    .doOnError {  isShowLoader.set(false)}
                                    .onErrorResumeNext(Observable.empty())
                        }
                        .flatMap { it -> val setOfCities = HashSet<String>()
                            it.predictions.mapTo(setOfCities) { it.structured_formatting.main_text }
                            Observable.fromArray(setOfCities)
                        }
                        .switchMap { it->
                            val listOfObservables = ArrayList<Observable<WeatherModel>>()
                            it.mapTo(listOfObservables) {
                                cache.getWeather(apiService.getWeather(WeatherService.weatherBaseUrl, it, weatherKey), DynamicKey(it))
                            }
                            Observable.zip(listOfObservables, {r -> r})
                                    .doOnError {isShowLoader.set(false)}
                                    .onErrorResumeNext(Observable.empty())
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result -> run {
                                    isShowLoader.set(false)
                                    val weather = ArrayList<WeatherModel>()
                                    result.mapTo(weather) { it as WeatherModel}
                                    resultWeather.value = weather
                                    //Logger.i("result $weather")
                                }},
                                { error -> run{
                                    isShowLoader.set(false)
                                    //Logger.i("error $error")
                                }}
                        )
        )
    }

    fun getWeather(): LiveData<ArrayList<WeatherModel>> {
        return resultWeather
    }

    override fun onCleared() {
        disposables.clear()
    }
}