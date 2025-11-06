package com.erlon.segura.util;

import redis.clients.jedis.Jedis;

public class SimpleRedisLimiter {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;
    private static final int MAX_REQUESTS = 3;
    private static final int WINDOW_SECONDS = 60;

    public static boolean isAllowed(String key) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            String redisKey = "rate:" + key;
            long current = jedis.incr(redisKey);
            if (current == 1) {
                jedis.expire(redisKey, WINDOW_SECONDS);
            }
            return current <= MAX_REQUESTS;
        } catch (Exception e) {
            System.out.println("Erro Redis (limiter): " + e.getMessage());
            return true;
        }
    }
}
