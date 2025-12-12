package com.proyecto.SafeRoute.controller;

import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para los dashboards según rol de usuario
 * Maneja las vistas principales de Admin, Padre y Conductor
 */
@Controller
public class DashboardController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Dashboard del Administrador
     * Acceso: ROLE_ADMIN
     * @param model Modelo para pasar datos a la vista
     * @param authentication Datos del usuario autenticado
     * @return Vista DashboardAdmin.html
     */
    @GetMapping("/admin/dashboard")
    public String dashboardAdmin(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuario", usuario);
            });
        }
        return "DashboardAdmin";
    }

    /**
     * Dashboard del Padre de Familia
     * Acceso: ROLE_PADRE
     * @param model Modelo para pasar datos a la vista
     * @param authentication Datos del usuario autenticado
     * @return Vista DashboardPadre.html
     */
    @GetMapping("/padre/dashboard")
    public String dashboardPadre(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuario", usuario);
            });
        }
        return "DashboardPadre";
    }

    /**
     * Dashboard del Conductor
     * Acceso: ROLE_CONDUCTOR
     * @param model Modelo para pasar datos a la vista
     * @param authentication Datos del usuario autenticado
     * @return Vista DashboardConductor.html
     */
    @GetMapping("/conductor/dashboard")
    public String dashboardConductor(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuario", usuario);
            });
        }
        return "DashboardConductor";
    }
}