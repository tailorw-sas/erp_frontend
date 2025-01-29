package com.kynsoft.report;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReportsApplication {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ReportsApplication.class, args);
    }

    @PostConstruct
    public void loadJDBCDriver() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver cargado correctamente.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver de PostgreSQL", e);
        }
    }
}