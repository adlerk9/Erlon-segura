package com.erlon.segura;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserRepository {

    public void update(User u) throws Exception {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE users SET token = ? WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, u.token); // pode ser null para logout
                ps.setString(2, u.email);
                ps.executeUpdate();
            }
        }
    }

    public void save(User u) throws Exception {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO users (name, email, document, password, token) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, u.name);
                ps.setString(2, u.email);
                ps.setString(3, u.document);
                ps.setString(4, u.password);
                ps.setString(5, u.token);
                ps.executeUpdate();
            }
        }
    }

    public User findByEmail(String email) throws Exception {
        try (Connection conn = Database.getConnection()) {
            var sql = "SELECT * FROM users WHERE email = ?";
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User u = new User();
                        u.name = rs.getString("name");
                        u.email = rs.getString("email");
                        u.document = rs.getString("document");
                        u.password = rs.getString("password");
                        u.token = rs.getString("token");
                        return u;
                    }
                }
            }
        }
        return null;
    }

    public User findByToken(String token) throws Exception {
        try (Connection conn = Database.getConnection()) {
            var sql = "SELECT * FROM users WHERE token = ?";
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User u = new User();
                        u.name = rs.getString("name");
                        u.email = rs.getString("email");
                        u.document = rs.getString("document");
                        u.password = rs.getString("password");
                        u.token = rs.getString("token");
                        return u;
                    }
                }
            }
        }
        return null;
    }

    public void updatePassword(String email, String document, String newPass) throws Exception {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE users SET password = ? WHERE email = ? AND document = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newPass);
                ps.setString(2, email);
                ps.setString(3, document);
                ps.executeUpdate();
            }
        }
    }
}
