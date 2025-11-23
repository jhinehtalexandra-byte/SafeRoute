package com.proyecto.SafeRoute.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /**
     * Redirige al usuario según su rol después del login exitoso
     */
    @GetMapping("/auth-success")
    public String authSuccess(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener el rol del usuario
        String rol = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("");
        
        // Redirigir según el rol
        if ("ADMIN".equals(rol)) {
            return "redirect:/dashboard";
        } else if ("PARENT".equals(rol)) {
            return "redirect:/Profile";
        } else if ("DRIVER".equals(rol)) {
            return "redirect:/Profile";
        }
        
        return "redirect:/login";
    }
}