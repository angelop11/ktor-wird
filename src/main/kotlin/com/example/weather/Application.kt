package com.example.weather

import io.ktor.server.application.*
import com.example.weather.config.RedisConfig
import com.example.weather.controllers.weatherRoutes
import com.example.weather.services.WeatherService.fetchAndStoreWeatherData
import kotlinx.coroutines.*
import com.example.weather.clients.CityCoordinates


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    RedisConfig.initialize() 
    weatherRoutes()

    environment.monitor.subscribe(ApplicationStopping) {
        RedisConfig.shutdown()
    }
    
     GlobalScope.launch {
        while (true) {
            CityCoordinates.cities.forEach { (city, coordinates) ->
                launch {
                    fetchAndStoreWeatherData(city)
                }
            }
            delay(5 * 60 * 1000)
        }
    }

}