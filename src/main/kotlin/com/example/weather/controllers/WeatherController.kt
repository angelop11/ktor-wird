package com.example.weather.controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.weather.services.WeatherService

fun Application.weatherRoutes() {
    routing {
        get("/weather/{city}") {
            val city = call.parameters["city"]?.lowercase() 
            val data = WeatherService.getWeatherData(city)
            if (data != null) {
                call.respond(data)
            } else {
                call.respond("Data not available for $city")
            }
        }
    }
}
