package com.proyecto.SafeRoute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación SafeRoute.
 * Sistema de gestión de rutas seguras con autenticación y reportes.
 */
@SpringBootApplication
public class SafeRouteApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * 
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(SafeRouteApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  SafeRoute Application Started Successfully");
        System.out.println("  Accede a: http://localhost:8080");
        System.out.println("==============================================");
    }

}