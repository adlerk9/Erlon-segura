package com.erlon.segura;
import java.sql.*;

public class UserRepository {
    Connection conn = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/erlonsegura",
            "postgres", "123456");
    public UserRepository() throws Exception {
        conn.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY, " +
            "name VARCHAR(100), " +
            "email VARCHAR(100), " +
            "document VARCHAR(50), " +
            "password VARCHAR(100), " +
            "token TEXT" +
            ")"
        );
    }

    public void save(User u) throws Exception {
        var st = conn.prepareStatement("INSERT INTO users (name,email,document,password,token) VALUES (?,?,?,?,?)");
        st.setString(1, u.name);
        st.setString(2, u.email);
        st.setString(3, u.document);
        st.setString(4, u.password);
        st.setString(5, u.token);
        st.execute();
    }

    public User findByEmail(String email) throws Exception {
        var rs = conn.createStatement().executeQuery("SELECT * FROM users WHERE email='" + email + "'");
        return rs.next() ? map(rs) : null;
    }

    public User findByToken(String token) throws Exception {
        var rs = conn.createStatement().executeQuery("SELECT * FROM users WHERE token='" + token + "'");
        return rs.next() ? map(rs) : null;
    }

    public void updatePassword(String email, String doc, String newPass) throws Exception {
        conn.createStatement().executeUpdate(
            "UPDATE users SET password='" + newPass + "' WHERE email='" + email + "' AND document='" + doc + "'");
    }

    private User map(ResultSet rs) throws Exception {
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
