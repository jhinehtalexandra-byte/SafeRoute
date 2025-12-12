package com.proyecto.SafeRoute.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Controlador para la página principal de RutaEscolar
 * Maneja las rutas públicas accesibles sin autenticación
 */
@Controller
public class HomeController {


    /**
     * Página de inicio principal
     * Ruta: localhost:8080/
     * @return Vista home.html
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }


    /**
     * Ruta alternativa para la página de inicio
     * Ruta: localhost:8080/home
     * @return Vista home.html
     */
    @GetMapping("/home")
    public String homeAlternative() {
        return "home";
    }
   
    /**
     * Página de error personalizada
     * @return Vista error.html
     */
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
