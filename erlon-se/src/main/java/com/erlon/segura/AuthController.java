package com.erlon.segura;

import static spark.Spark.*;
import com.google.gson.Gson;

public class AuthController {

    public static void start() throws Exception {
        port(4567);
        Gson gson = new Gson();
        AuthService service = new AuthService();
        SecureRateLimiter limiter = new SecureRateLimiter();

        post("/api/v1/auth/signup", (req, res) -> {
            var data = gson.fromJson(req.body(), java.util.Map.class);
            String name = (String) data.get("name");
            String email = (String) data.get("email");
            String doc = (String) data.get("document");
            String pass = (String) data.get("password");
            return service.signup(name, email, doc, pass);
        });

        post("/api/v1/auth/login", (req, res) -> {
            var data = gson.fromJson(req.body(), java.util.Map.class);
            String email = (String) data.get("email");
            String pass = (String) data.get("password");
            String ip = req.ip();
            if (!limiter.isAllowed(ip)) {
                res.status(429);
                return "Muitas tentativas. Tente novamente em instantes.";
            }
            String token = service.login(email, pass);
            if (token == null) {
                res.status(401);
                return "Credenciais inválidas";
            }
            return token;
        });

        post("/api/v1/auth/recuperar-senha", (req, res) -> {
            var data = gson.fromJson(req.body(), java.util.Map.class);
            String email = (String) data.get("email");
            String doc = (String) data.get("document");
            String newPass = (String) data.get("password");
            return service.recover(email, doc, newPass);
        });

        get("/api/v1/auth/me", (req, res) -> {
            String token = req.headers("Authorization");
            if (token == null || token.isEmpty()) {
                res.status(400);
                return "Token ausente";
            }
            return service.getUserInfoFromToken(token);
        });

        post("/api/v1/auth/logout", (req, res) -> {
            String token = req.headers("Authorization");
            service.logout(token);
            return "Logout realizado com sucesso.";
        });
    }
}
