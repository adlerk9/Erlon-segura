package com.erlon.segura;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConsoleClient {
    private static final String BASE = "http://localhost:4567/api/v1/auth";
    private static String lastToken = null;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n== erlon-segura CLI ==");
            System.out.println("1) Signup");
            System.out.println("2) Login");
            System.out.println("3) Recuperar senha");
            System.out.println("4) Me (usar token)");
            System.out.println("5) Logout");
            System.out.println("6) Sair");
            System.out.print("Escolha: ");
            String opt = sc.nextLine().trim();

            try {
                switch (opt) {
                    case "1" -> signup(sc);
                    case "2" -> login(sc);
                    case "3" -> recover(sc);
                    case "4" -> me();
                    case "5" -> logout();
                    case "6" -> {
                        System.out.println("Saindo...");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }

    private static void signup(Scanner sc) throws IOException {
        System.out.print("name: "); String name = sc.nextLine();
        System.out.print("email: "); String email = sc.nextLine();
        System.out.print("document: "); String doc = sc.nextLine();
        System.out.print("password: "); String pass = sc.nextLine();

        String body = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"document\":\"%s\",\"password\":\"%s\"}",
                escapeJson(name), escapeJson(email), escapeJson(doc), escapeJson(pass)
        );
        String resp = post(BASE + "/signup", body, null);
        System.out.println("Resposta: " + resp);
    }

    private static void login(Scanner sc) throws IOException {
        System.out.print("email: "); String email = sc.nextLine();
        System.out.print("password: "); String pass = sc.nextLine();

        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}",
                escapeJson(email), escapeJson(pass));

        String resp = post(BASE + "/login", body, null);
        System.out.println("Resposta: " + resp);

        if (!resp.contains("erro") && !resp.contains("inválido")) {
            lastToken = stripQuotes(resp);
            System.out.println("Token salvo localmente: " + lastToken);
        }
    }

    private static void recover(Scanner sc) throws IOException {
        System.out.print("email: "); String email = sc.nextLine();
        System.out.print("document: "); String doc = sc.nextLine();
        System.out.print("nova senha: "); String np = sc.nextLine();

        String body = String.format(
                "{\"email\":\"%s\",\"document\":\"%s\",\"new_password\":\"%s\"}",
                escapeJson(email), escapeJson(doc), escapeJson(np)
        );
        String resp = post(BASE + "/recuperar-senha", body, null);
        System.out.println("Resposta: " + resp);
    }

    private static void me() throws IOException {
        if (lastToken == null) {
            System.out.println("Nenhum token salvo.");
            return;
        }
        String resp = get(BASE + "/me", lastToken);
        System.out.println("Resposta: " + resp);
    }

    private static void logout() throws IOException {
        if (lastToken == null) {
            System.out.println("Nenhum token para logout.");
            return;
        }
        String resp = post(BASE + "/logout", "{}", lastToken);
        System.out.println("Logout: " + resp);
        lastToken = null;
    }

    private static String post(String urlStr, String jsonBody, String token) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        if (token != null) con.setRequestProperty("Authorization", "Bearer " + token);
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(con);
    }

    private static String get(String urlStr, String token) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        if (token != null) con.setRequestProperty("Authorization", "Bearer " + token);
        return readResponse(con);
    }

    private static String readResponse(HttpURLConnection con) throws IOException {
        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 400) ? con.getInputStream() : con.getErrorStream();
        if (is == null) return "HTTP " + code;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static String stripQuotes(String s) {
        if (s == null) return null;
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
