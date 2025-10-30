package com.erlon.segura;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.erlon.segura.util.RateLimiter;

public class AuthController {
    public static void start() throws Exception {
        port(4567);

        var gson = new Gson();
        var service = new AuthService();

        post("/api/v1/auth/signup", (req, res) -> {
            String ip = req.ip();
            if (!RateLimiter.isAllowed(ip)) {
                res.status(429);
                return "Muitas tentativas — tente novamente mais tarde.";
            }
            var user = gson.fromJson(req.body(), User.class);
            String token = service.signup(user.name, user.email, user.document, user.password);
            return token;
        });

        post("/api/v1/auth/login", (req, res) -> {
            String ip = req.ip();
            if (!RateLimiter.isAllowed(ip)) {
                res.status(429);
                return "Muitas tentativas — tente novamente mais tarde.";
            }
            var loginData = gson.fromJson(req.body(), java.util.Map.class);
            String email = (String) loginData.get("email");
            String password = (String) loginData.get("password");
            String token = service.login(email, password);
            if (token == null) {
                res.status(401);
                return "Credenciais inválidas";
            }
            return token;
        });

        post("/api/v1/auth/recuperar-senha", (req, res) -> {
            String ip = req.ip();
            if (!RateLimiter.isAllowed(ip)) {
                res.status(429);
                return "Muitas tentativas — tente novamente mais tarde.";
            }
            var data = gson.fromJson(req.body(), java.util.Map.class);
            String email = (String) data.get("email");
            String doc = (String) data.get("document");
            String newPass = (String) data.get("password");
            return service.recover(email, doc, newPass);
        });

        get("/api/v1/auth/me", (req, res) -> {
            String token = req.headers("Authorization");
            var user = service.me(token);
            if (user == null) {
                res.status(401);
                return "Token inválido";
            }
            return gson.toJson(user);
        });
    }
}
