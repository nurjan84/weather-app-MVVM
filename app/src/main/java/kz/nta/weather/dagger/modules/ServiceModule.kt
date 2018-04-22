package kz.nta.weather.dagger.modules

import dagger.Module
import dagger.Provides
import kz.nta.weather.api.WeatherService
import kz.nta.weather.dagger.scopes.PerActivity
import retrofit2.Retrofit

@Module
class ServiceModule {

    @PerActivity
    @Provides
    fun  getApiService(retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }

}