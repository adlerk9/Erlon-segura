package com.erlon.segura;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("=== Servidor iniciando na porta 4567 ===");

        Database.checkConnection();
        AuthController.start();
        System.out.println("=== Servidor pronto! Endpoints registrados ===");
    }
}
