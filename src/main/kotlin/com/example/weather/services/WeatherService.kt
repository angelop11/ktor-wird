package com.example.weather.services

import com.example.weather.config.RedisConfig
import com.example.weather.clients.WeatherApiClient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

object WeatherService {
    private val log = LoggerFactory.getLogger(WeatherService::class.java)

    suspend fun getWeatherData(city: String?): String? {
        return city?.let {
            RedisConfig.getClient().use { jedis ->
                jedis.get(it)
            }
        }
    }

    suspend fun fetchAndStoreWeatherData(city: String) {
        try {
            val data = RetryService.retry { WeatherApiClient.getWeatherData(city) }
            val jsonData = Json.encodeToString(data)
            RedisConfig.getClient().use { jedis ->
                jedis.setex(city, 300, jsonData)
            }
            log.info("Weather data fetched and stored for city: $city")
        } catch (e: Exception) {
            RedisConfig.getClient().use { jedis ->
                jedis.set("error_log:$city", "Error: ${e.message}, Timestamp: ${System.currentTimeMillis()}")
            }
            log.error("Error fetching and storing weather data for city: $city", e)
        }
    }
}
