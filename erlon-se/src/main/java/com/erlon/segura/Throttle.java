package com.erlon.segura;

import redis.clients.jedis.Jedis;

public class Throttle {
    private static final int MAX_REQUESTS = 3;
    private static final int WINDOW_SECONDS = 60;
    private static Jedis jedis = new Jedis("localhost", 6379);

    public static boolean isAllowed(String ip) {
        String key = "rate:" + ip;
        long requests = jedis.incr(key);
        if (requests == 1) {
            jedis.expire(key, WINDOW_SECONDS);
        }
        return requests <= MAX_REQUESTS;
    }
}
