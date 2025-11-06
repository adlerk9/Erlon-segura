package com.erlon.segura;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import redis.clients.jedis.Jedis;

public class AuthService {
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final Map<String, String> tokens = new ConcurrentHashMap<>();
    private final Jedis redis = new Jedis("localhost", 6379);
    private final SecureRateLimiter limiter = new SecureRateLimiter();

    public String signup(String name, String email, String document, String password) {
        if (users.containsKey(email)) {
            throw new RuntimeException("Usuário já existe");
        }
        users.put(email, password);
        return generateToken(email);
    }

    public String login(String email, String password) {
        if (!users.containsKey(email) || !users.get(email).equals(password)) {
            throw new RuntimeException("Credenciais inválidas");
        }
        return generateToken(email);
    }

    public boolean logout(String token) {
        if (token == null || !tokens.containsKey(token)) {
            return false;
        }
        tokens.remove(token);
        return true;
    }

    public boolean isAllowed(String token, String ip) {
        return limiter.isAllowed(ip);
    }

    public String recover(String email, String document, String newPassword) {
        if (!users.containsKey(email)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        users.put(email, newPassword);
        return generateToken(email);
    }

    public Map<String, Object> getUserInfoFromToken(String token) {
        if (token == null || !tokens.containsKey(token)) return null;
        String email = tokens.get(token);
        Map<String, Object> info = new HashMap<>();
        info.put("email", email);
        info.put("status", "ativo");
        return info;
    }

    private String generateToken(String email) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, email);
        return token;
    }
}
