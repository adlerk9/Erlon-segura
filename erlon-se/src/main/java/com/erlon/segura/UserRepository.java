package com.erlon.segura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepository {

    public void save(User u) throws Exception {
        try (Connection conn = Database.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name VARCHAR(100), email VARCHAR(100) UNIQUE, document VARCHAR(50), password VARCHAR(100), token TEXT)";
            try (PreparedStatement st = conn.prepareStatement(sql)) { st.execute(); }
            sql = "INSERT INTO users (name, email, document, password, token) VALUES (?, ?, ?, ?, ?)";
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
            String sql = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User u = new User();
                        u.id = rs.getInt("id");
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
        if (token == null) return null;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM users WHERE token = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User u = new User();
                        u.id = rs.getInt("id");
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

    public void update(User u) throws Exception {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE users SET token = ?, password = ? WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, u.token);
                ps.setString(2, u.password);
                ps.setString(3, u.email);
                ps.executeUpdate();
            }
        }
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
