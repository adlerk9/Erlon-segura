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
        if (u == null) return null;
        if (!u.password.equals(pass)) return null;
        return u.token;
    }

    public String recover(String email, String doc, String newPass) throws Exception {
        repo.updatePassword(email, doc, newPass);
        return Base64.getEncoder().encodeToString((email + ":" + doc).getBytes());
    }

    public User me(String token) throws Exception {
        return repo.findByToken(token);
    }
}
