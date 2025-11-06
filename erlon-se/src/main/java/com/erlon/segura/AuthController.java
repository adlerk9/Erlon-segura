package com.erlon.segura;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.erlon.segura.Throttle;

public class AuthController {
    public static void start() throws Exception {
        port(4567);

        var gson = new Gson();
        var service = new AuthService();

        post("/api/v1/auth/signup", (req, res) -> {
            String ip = req.ip();
            if (!Throttle.isAllowed(ip)) {
                res.status(429);
                return "Muitas tentativas — tente novamente mais tarde.";
            }
            var user = gson.fromJson(req.body(), User.class);
            return service.signup(user.name, user.email, user.document, user.password);
        });

        post("/api/v1/auth/login", (req, res) -> {
            String ip = req.ip();
            if (!Throttle.isAllowed(ip)) {
                res.status(429);
                return "Muitas tentativas — tente novamente mais tarde.";
            }
            var loginData = gson.fromJson(req.body(), java.util.Map.class);
            return service.login((String) loginData.get("login"), (String) loginData.get("password"));
        });

        post("/api/v1/auth/recuperar-senha", (req, res) -> {
            String ip = req.ip();
            if (!Throttle.isAllowed(ip)) {
                res.status(429);
                return "Muitas tentativas — tente novamente mais tarde.";
            }
            var data = gson.fromJson(req.body(), java.util.Map.class);
            return service.recover(
                    (String) data.get("email"),
                    (String) data.get("document"),
                    (String) data.get("new_password")
            );
        });

        post("/api/v1/auth/logout", (req, res) -> {
            String token = req.headers("Authorization");
            return service.logout(token);
        });

        get("/api/v1/auth/me", (req, res) -> {
            String ip = req.ip();
            if (!Throttle.isAllowed(ip)) {
                res.status(429);
                return "Muitas tentativas — tente novamente mais tarde.";
            }
            String token = req.headers("Authorization");
            return service.me(token);
        });
    }
}
