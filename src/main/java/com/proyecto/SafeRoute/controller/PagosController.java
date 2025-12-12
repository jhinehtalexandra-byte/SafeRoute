package com.proyecto.SafeRoute.controller;

import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para la gestión de pagos
 * Accesible para ADMIN y PADRE
 */
@Controller
@RequestMapping("/pagos")
public class PagosController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Muestra la página de gestión de pagos
     */
    @GetMapping
    public String gestionPagos(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuario", usuario);
            });
        }
        return "gestionPagos";
    }
}