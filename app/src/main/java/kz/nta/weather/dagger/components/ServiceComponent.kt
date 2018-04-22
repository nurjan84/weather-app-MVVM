package kz.nta.weather.dagger.components

import dagger.Component
import kz.nta.weather.dagger.modules.ServiceModule
import kz.nta.weather.dagger.scopes.PerActivity
import kz.nta.weather.mvvm.viewModels.WeatherViewModel


@PerActivity
@Component(modules = [ServiceModule::class], dependencies = [AppComponent::class] )
interface ServiceComponent{
    fun injectWeatherViewModel(presenter: WeatherViewModel)
}