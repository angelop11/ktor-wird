package com.example.weather.clients


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import org.slf4j.LoggerFactory

object WeatherApiClient {
    private val apiKey = "yNaUNNkCikAqGY5cFjbr3lWdS8ZitXdI"
    private val log = LoggerFactory.getLogger(WeatherApiClient::class.java)


    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getWeatherData(city: String): WeatherResponse {
            
        if (Random.nextDouble() < 0.2) throw Exception("The API Request Failed")
        log.info("Starting request for weather data for city: $city")

        val cityData = CityCoordinates.cities[city]
        log.info("City coordinates found: $cityData")

        if (cityData != null) {
            return try {
                log.info("Making API request for city coordinates: ${cityData.latitude}, ${cityData.longitude}")
                client.get("https://api.tomorrow.io/v4/timelines") {
                    parameter("location", "${cityData.latitude},${cityData.longitude}")
                    parameter("fields", "temperature")
                    parameter("timesteps", "current")
                    parameter("units", "metric")
                    parameter("apikey", apiKey)
                    contentType(ContentType.Application.Json)
                }.body<WeatherResponse>()
            } catch (e: ClientRequestException) {
                log.error("Client request error: ${e.response.status}")
                throw Exception("Client request error: ${e.response.status}")
            } catch (e: ServerResponseException) {
                log.error("Server error: ${e.response.status}")
                throw Exception("Server error: ${e.response.status}")
            } catch (e: Exception) {
                log.error("Error fetching weather data: ${e.message}")
                throw Exception("Error fetching weather data: ${e.message}")
            }
        } else {
            log.error("City not found: $city")
            throw Exception("City not found")
        }
    }
}

@Serializable
data class WeatherResponse(
    val data: Data
)

@Serializable
data class Data(
    val timelines: List<Timeline>
)

@Serializable
data class Timeline(
    val timestep: String,
    val endTime: String,
    val startTime: String,
    val intervals: List<Interval>
)

@Serializable
data class Interval(
    val startTime: String,
    val values: Values
)

@Serializable
data class Values(
    val temperature: Double
)

object CityCoordinates {
    val cities = mapOf(
        "santiago" to Coordinates(-33.4489, -70.6693),
        "zurich" to Coordinates(47.3769, 8.5417),
        "auckland" to Coordinates(-36.8485, 174.7633),
        "sydney" to Coordinates(-33.8688, 151.2093),
        "london" to Coordinates(51.5074, -0.1278),
        "georgia" to Coordinates(33.7490, -84.3880)
    )
}

@Serializable
data class Coordinates(val latitude: Double, val longitude: Double)
