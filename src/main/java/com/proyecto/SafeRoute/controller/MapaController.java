package com.proyecto.SafeRoute.controller;

import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para el mapa del conductor
 * Accesible para CONDUCTOR y ADMIN
 */
@Controller
@RequestMapping("/mapa")
public class MapaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Muestra el mapa del conductor
     * Ruta: /mapa/conductor
     * Vista: MapaConductor.html
     */
    @GetMapping("/conductor")
    public String mapaConductor(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuario", usuario);
            });
        }
        return "MapaConductor";
    }
}