package com.example.weather.services

import kotlinx.coroutines.delay

object RetryService {
    suspend fun <T> retry(retries: Int = 3, block: suspend () -> T): T {
        repeat(retries) {
            try {
                return block()
            } catch (e: Exception) {
                if (it == retries - 1) throw e
                delay(1000)
            }
        }
        throw Exception("Failed after $retries retries")
    }
}
