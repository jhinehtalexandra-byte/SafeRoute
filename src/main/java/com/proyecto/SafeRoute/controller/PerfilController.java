package com.proyecto.SafeRoute.controller;


import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controlador para la gestión del perfil de usuario
 */
@Controller
@RequestMapping("/perfil")
public class PerfilController {


    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    /**
     * Muestra la página del perfil del usuario autenticado
     */
    @GetMapping
    public String mostrarPerfil(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuario", usuario);
            });
        }
        return "miPerfil";
    }


    /**
     * Actualiza la información del perfil
     */
    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
       
        try {
            if (authentication == null) {
                redirectAttributes.addFlashAttribute("error", "No autenticado");
                return "redirect:/login";
            }


            String username = authentication.getName();
            Usuario usuario = usuarioRepository.findByUserName(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


            // Validar si el email ya existe (y no es el mismo usuario)
            usuarioRepository.findByEmail(email).ifPresent(u -> {
                if (!u.getId().equals(usuario.getId())) {
                    throw new RuntimeException("El email ya está en uso");
                }
            });


            // Actualizar datos
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);


            usuarioRepository.save(usuario);


            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
        }


        return "redirect:/perfil";
    }


    /**
     * Cambia la contraseña del usuario
     */
    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
       
        try {
            if (authentication == null) {
                redirectAttributes.addFlashAttribute("error", "No autenticado");
                return "redirect:/login";
            }


            String username = authentication.getName();
            Usuario usuario = usuarioRepository.findByUserName(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


            // Verificar contraseña actual
            if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/perfil";
            }


            // Actualizar contraseña
            usuario.setPassword(passwordEncoder.encode(passwordNueva));
            usuarioRepository.save(usuario);


            redirectAttributes.addFlashAttribute("mensaje", "Contraseña cambiada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar contraseña: " + e.getMessage());
        }


        return "redirect:/perfil";
    }
}