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

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de usuarios (CRUD completo)
 * Solo accesible para usuarios con rol ADMIN
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Lista todos los usuarios del sistema
     * Vista: Usuarios.html (CRUD)
     */
    @GetMapping
    public String listarUsuarios(Model model, Authentication authentication) {
        // Agregar información del usuario actual al modelo
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuarioActual", usuario);
            });
        }
        
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "Usuarios";
    }

    /**
     * Muestra el formulario para crear un nuevo usuario
     * Vista: FormCrearUsuario.html
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, Authentication authentication) {
        // Agregar información del usuario actual
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuarioActual", usuario);
            });
        }
        
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("esNuevo", true);
        return "FormCrearUsuario";
    }

    /**
     * Muestra el formulario para editar un usuario existente
     * Vista: FormCrearUsuario.html
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Long id, 
            Model model, 
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "ID de usuario inválido");
            return "redirect:/usuarios";
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (!usuarioOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios";
        }
        
        // Agregar información del usuario actual
        if (authentication != null) {
            String username = authentication.getName();
            usuarioRepository.findByUserName(username).ifPresent(usuario -> {
                model.addAttribute("usuarioActual", usuario);
            });
        }
        
        model.addAttribute("usuario", usuarioOpt.get());
        model.addAttribute("esNuevo", false);
        return "FormCrearUsuario";
    }

    /**
     * Guarda o actualiza un usuario
     */
    @PostMapping("/guardar")
    public String guardarUsuario(
            @RequestParam(required = false) Long id,
            @RequestParam String userName,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String password,
            @RequestParam String rol,
            @RequestParam(defaultValue = "true") Boolean activo,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validaciones básicas
            if (userName == null || userName.trim().isEmpty()) {
                throw new RuntimeException("El nombre de usuario es requerido");
            }
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new RuntimeException("El nombre es requerido");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("El email es requerido");
            }
            if (rol == null || rol.trim().isEmpty()) {
                throw new RuntimeException("El rol es requerido");
            }
            
            Usuario usuario;
            
            if (id != null && id > 0) {
                // Actualizar usuario existente
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
                
                if (!usuarioOpt.isPresent()) {
                    throw new RuntimeException("Usuario no encontrado");
                }
                
                usuario = usuarioOpt.get();
                
                // Validar que el username no esté en uso por otro usuario
                Optional<Usuario> usuarioPorUsername = usuarioRepository.findByUserName(userName.trim());
                if (usuarioPorUsername.isPresent() && !usuarioPorUsername.get().getId().equals(id)) {
                    throw new RuntimeException("El nombre de usuario ya está en uso por otro usuario");
                }
                
                // Validar que el email no esté en uso por otro usuario
                Optional<Usuario> usuarioPorEmail = usuarioRepository.findByEmail(email.trim());
                if (usuarioPorEmail.isPresent() && !usuarioPorEmail.get().getId().equals(id)) {
                    throw new RuntimeException("El correo electrónico ya está en uso por otro usuario");
                }
                
            } else {
                // Crear nuevo usuario
                usuario = new Usuario();
                
                // Validar que el username no exista
                if (usuarioRepository.existsByUserName(userName.trim())) {
                    throw new RuntimeException("El nombre de usuario ya está en uso");
                }
                
                // Validar que el email no exista
                if (usuarioRepository.existsByEmail(email.trim())) {
                    throw new RuntimeException("El correo electrónico ya está en uso");
                }
                
                // Para nuevos usuarios, la contraseña es obligatoria
                if (password == null || password.trim().isEmpty()) {
                    throw new RuntimeException("La contraseña es requerida para nuevos usuarios");
                }
            }
            
            // Actualizar datos (trim para evitar espacios en blanco)
            usuario.setUserName(userName.trim());
            usuario.setNombre(nombre.trim());
            usuario.setEmail(email.trim());
            usuario.setTelefono(telefono != null ? telefono.trim() : null);
            usuario.setRol(rol);
            usuario.setActivo(activo != null ? activo : true);
            
            // Solo actualizar contraseña si se proporcionó una nueva
            if (password != null && !password.trim().isEmpty()) {
                // Validar longitud mínima de contraseña
                if (password.length() < 4) {
                    throw new RuntimeException("La contraseña debe tener al menos 4 caracteres");
                }
                usuario.setPassword(passwordEncoder.encode(password));
            }
            
            usuarioRepository.save(usuario);
            
            String mensaje = id != null && id > 0 ? 
                "Usuario actualizado correctamente" : 
                "Usuario creado correctamente";
            redirectAttributes.addFlashAttribute("mensaje", mensaje);
            redirectAttributes.addFlashAttribute("tipo", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
            
            // Si hay error, redirigir al formulario correspondiente
            if (id != null && id > 0) {
                return "redirect:/usuarios/editar/" + id;
            } else {
                return "redirect:/usuarios/nuevo";
            }
        }
        
        return "redirect:/usuarios";
    }

    /**
     * Elimina un usuario
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(
            @PathVariable Long id, 
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (id == null || id <= 0) {
                throw new RuntimeException("ID de usuario inválido");
            }
            
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            
            if (!usuarioOpt.isPresent()) {
                throw new RuntimeException("Usuario no encontrado");
            }
            
            // Prevenir que el usuario se elimine a sí mismo
            if (authentication != null) {
                String username = authentication.getName();
                Optional<Usuario> usuarioActual = usuarioRepository.findByUserName(username);
                if (usuarioActual.isPresent() && usuarioActual.get().getId().equals(id)) {
                    throw new RuntimeException("No puedes eliminar tu propio usuario");
                }
            }
            
            usuarioRepository.deleteById(id);
            
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/usuarios";
    }

    /**
     * Activa o desactiva un usuario
     */
    @PostMapping("/toggle-estado/{id}")
    public String toggleEstadoUsuario(
            @PathVariable Long id, 
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (id == null || id <= 0) {
                throw new RuntimeException("ID de usuario inválido");
            }
            
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            
            if (!usuarioOpt.isPresent()) {
                throw new RuntimeException("Usuario no encontrado");
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Prevenir que el usuario se desactive a sí mismo
            if (authentication != null) {
                String username = authentication.getName();
                Optional<Usuario> usuarioActual = usuarioRepository.findByUserName(username);
                if (usuarioActual.isPresent() && usuarioActual.get().getId().equals(id)) {
                    throw new RuntimeException("No puedes cambiar el estado de tu propio usuario");
                }
            }
            
            usuario.setActivo(!usuario.getActivo());
            usuarioRepository.save(usuario);
            
            String estado = usuario.getActivo() ? "activado" : "desactivado";
            redirectAttributes.addFlashAttribute("mensaje", "Usuario " + estado + " correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/usuarios";
    }
}