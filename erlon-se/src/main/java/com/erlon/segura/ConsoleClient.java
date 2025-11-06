package com.erlon.segura;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleClient {
    private static final String BASE = "http://localhost:4567/api/v1/auth";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String lastToken = null;

        while(true) {
            System.out.println("\n== erlon-segura CLI ==");
            System.out.println("1) Signup");
            System.out.println("2) Login");
            System.out.println("3) Recuperar senha");
            System.out.println("4) Me (usar token)");
            System.out.println("5) Sair");
            System.out.print("Escolha: ");
            String opt = sc.nextLine().trim();
            try {
                if("1".equals(opt)) {
                    System.out.print("name: "); String name = sc.nextLine();
                    System.out.print("email: "); String email = sc.nextLine();
                    System.out.print("document: "); String doc = sc.nextLine();
                    System.out.print("password: "); String pass = sc.nextLine();
                    String body = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"document\":\"%s\",\"password\":\"%s\"}",
                            escapeJson(name), escapeJson(email), escapeJson(doc), escapeJson(pass));
                    String resp = post(BASE + "/signup", body, null);
                    lastToken = stripQuotes(resp);
                    System.out.println("Token salvo localmente: " + lastToken);
                } else if("2".equals(opt)) {
                    System.out.print("email: "); String email = sc.nextLine();
                    System.out.print("password: "); String pass = sc.nextLine();
                    String body = String.format("{\"login\":\"%s\",\"password\":\"%s\"}",
                            escapeJson(email), escapeJson(pass));
                    String resp = post(BASE + "/login", body, null);
                    if(resp.equals("Credenciais inválidas")) System.out.println(resp);
                    else {
                        lastToken = stripQuotes(resp);
                        System.out.println("Login realizado! Token salvo: " + lastToken);
                    }
                } else if("3".equals(opt)) {
                    System.out.print("email: "); String email = sc.nextLine();
                    System.out.print("document: "); String doc = sc.nextLine();
                    System.out.print("new password: "); String np = sc.nextLine();
                    String body = String.format("{\"email\":\"%s\",\"document\":\"%s\",\"new_password\":\"%s\"}",
                            escapeJson(email), escapeJson(doc), escapeJson(np));
                    String resp = post(BASE + "/recuperar-senha", body, null);
                    lastToken = stripQuotes(resp);
                    System.out.println("Token atualizado: " + lastToken);
                } else if("4".equals(opt)) {
                    if(lastToken == null) {
                        System.out.println("Nenhum token disponível.");
                    } else {
                        String resp = get(BASE + "/me", lastToken);
                        System.out.println("Resposta: " + resp);
                    }
                } else if("5".equals(opt)) break;
            } catch(Exception e) {
                e.printStackTrace(System.out);
            }
        }
        sc.close();
    }

    private static String post(String urlStr, String jsonBody, String token) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        if(token != null) con.setRequestProperty("Authorization", "SDWork " + token);
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(con);
    }

    private static String get(String urlStr, String token) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        if(token != null) con.setRequestProperty("Authorization", "SDWork " + token);
        return readResponse(con);
    }

    private static String readResponse(HttpURLConnection con) throws IOException {
        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 400) ? con.getInputStream() : con.getErrorStream();
        if(is == null) return "HTTP " + code;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static String stripQuotes(String s) {
        if(s == null) return null;
        s = s.trim();
        if((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length()-1);
        }
        return s;
    }

    private static String escapeJson(String s) {
        if(s == null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"");
    }
}
