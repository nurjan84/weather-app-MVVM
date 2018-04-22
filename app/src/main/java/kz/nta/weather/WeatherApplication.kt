package kz.nta.weather

import android.app.Application
import kz.nta.weather.api.WeatherService
import kz.nta.weather.dagger.components.AppComponent
import kz.nta.weather.dagger.components.DaggerAppComponent
import kz.nta.weather.dagger.modules.AppModule

class WeatherApplication : Application(){

    companion object {
        lateinit var appComponent: AppComponent
        fun getApplicationComponent(): AppComponent {
            return appComponent
        }
    }

    override fun onCreate() {
        super.onCreate()
        createAppComponent()
    }

    private fun createAppComponent(){
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this, WeatherService.baseUrl))
                .build()
    }

}