package com.erlon.segura;

import java.util.Base64;

public class AuthService {
    UserRepository repo = new UserRepository();

    public AuthService() throws Exception {}

    public String signup(String name, String email, String doc, String pass) throws Exception {
        User u = new User();
        u.name = name;
        u.email = email;
        u.document = doc;
        u.password = pass;
        u.token = Base64.getEncoder().encodeToString((email + ":" + doc).getBytes());
        repo.save(u);
        return u.token;
    }

    public String login(String email, String pass) throws Exception {
        User u = repo.findByEmail(email);
        return (u != null && u.password.equals(pass)) ? u.token : null;
    }

    public String recover(String email, String doc, String newPass) throws Exception {
        repo.updatePassword(email, doc, newPass);
        return Base64.getEncoder().encodeToString((email + ":" + doc).getBytes());
    }

    public User me(String token) throws Exception {
        return repo.findByToken(token);
    }

    public String logout(String token) throws Exception {
        if (token == null || token.isEmpty()) return "Token inválido";
        User u = repo.findByToken(token);
        if (u == null) return "Token inválido";
        u.token = null;
        repo.update(u);
        return "Logout realizado com sucesso";
    }
}
