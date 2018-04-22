package kz.nta.weather.dagger.components

import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import kz.nta.weather.api.CacheProviders
import kz.nta.weather.dagger.modules.AppModule
import kz.nta.weather.dagger.modules.NetworkModule
import kz.nta.weather.dagger.scopes.AppScope

import retrofit2.Retrofit

@AppScope
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent{
    fun getRetrofit(): Retrofit
    fun getContext(): Context
    fun getPrefs(): SharedPreferences
    fun getCache(): CacheProviders
}