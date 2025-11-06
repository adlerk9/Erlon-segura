package com.erlon.segura.util;

import redis.clients.jedis.Jedis;

public class Throttle {
    private static final int MAX_REQUESTS = 3;
    private static final int WINDOW_SECONDS = 60;
    private static final Jedis jedis = new Jedis("localhost", 6379);

    public static boolean isAllowed(String ip, String routeKey) {
        String key = "throttle:" + routeKey + ":" + ip;
        long count = jedis.incr(key);
        if (count == 1) jedis.expire(key, WINDOW_SECONDS);
        return count <= MAX_REQUESTS;
    }

    public static boolean isAllowed(String ip) {
        return isAllowed(ip, "default");
    }
}
