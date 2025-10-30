package com.erlon.segura;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private static final int MAX_ATTEMPTS = 3;
    private static final long WINDOW_MS = 60_000;
    private static final Map<String, UserAttempts> attempts = new ConcurrentHashMap<>();

    public static boolean isAllowed(String ip) {
        long now = System.currentTimeMillis();
        UserAttempts ua = attempts.getOrDefault(ip, new UserAttempts(0, now));
        if (now - ua.timestamp > WINDOW_MS) {
            ua.count = 1;
            ua.timestamp = now;
        } else {
            ua.count++;
        }
        attempts.put(ip, ua);
        return ua.count <= MAX_ATTEMPTS;
    }

    private static class UserAttempts {
        int count;
        long timestamp;
        UserAttempts(int count, long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }
}
