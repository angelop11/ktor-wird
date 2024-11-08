package com.example.weather.config

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object RedisConfig {
    private lateinit var jedisPool: JedisPool

    fun initialize() {
        val poolConfig = JedisPoolConfig().apply {
            maxTotal = 10
            maxIdle = 5
            minIdle = 1
            testOnBorrow = true
        }
        jedisPool = JedisPool(poolConfig, "localhost", 6379)
    }

    fun getClient(): Jedis {
        return jedisPool.resource
    }

    fun shutdown() {
        jedisPool.close()
    }
}
