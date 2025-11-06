package com.erlon.segura.util;

import redis.clients.jedis.Jedis;

public class RateLimiterRedis {
    private static final int MAX_REQUESTS = 3;
    private static final int WINDOW_SECONDS = 60;
    private static final Jedis jedis = new Jedis("localhost", 6379);

    public static boolean isAllowed(String ip, String action) {
        String key = "ratelimit:" + action + ":" + ip;
        long v = jedis.incr(key);
        if (v == 1) jedis.expire(key, WINDOW_SECONDS);
        return v <= MAX_REQUESTS;
    }
}
