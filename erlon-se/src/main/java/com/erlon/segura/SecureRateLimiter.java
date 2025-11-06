package com.erlon.segura;

import redis.clients.jedis.Jedis;

public class SecureRateLimiter {
    private static final int LIMIT = 3;
    private static final int WINDOW_SECONDS = 60;

    private final Jedis jedis;

    public SecureRateLimiter() {
        this.jedis = new Jedis("localhost", 6379);
    }

    public boolean isAllowed(String ip) {
        String key = "ratelimit:" + ip;
        long count = jedis.incr(key);

        if (count == 1) {
            jedis.expire(key, WINDOW_SECONDS);
        }

        return count <= LIMIT;
    }
}
