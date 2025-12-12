package com.proyecto.SafeRoute.controller;

import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para autenticación: Login y Registro
 */
@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Muestra la página de login
     * @param error Parámetro opcional si hay error de autenticación
     * @param logout Parámetro opcional si el usuario cerró sesión
     * @param model Modelo para pasar datos a la vista
     * @return Vista login.html
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("mensaje", "Sesión cerrada exitosamente");
        }
        
        return "login";
    }

    /**
     * Ruta POST para procesar el login (manejado por Spring Security)
     * Esta ruta es procesada automáticamente por Spring Security
     */
    @PostMapping("/login")
    public String loginPost() {
        // Spring Security maneja esto automáticamente
        return "redirect:/dashboard";
    }

    /**
     * Redirige al dashboard correspondiente según el rol del usuario
     * @param authentication Objeto de autenticación con datos del usuario
     * @return Redirección al dashboard apropiado
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        // Obtener el rol del usuario autenticado
        String rol = authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("");

        // Redirigir según el rol
        switch (rol) {
            case "ROLE_ADMIN":
                return "redirect:/admin/dashboard";
            case "ROLE_PADRE":
                return "redirect:/padre/dashboard";
            case "ROLE_CONDUCTOR":
                return "redirect:/conductor/dashboard";
            default:
                return "redirect:/login?error";
        }
    }

    /**
     * Muestra el formulario de registro
     * @return Vista Registrarse.html
     */
    @GetMapping("/registrarse")
    public String mostrarRegistro() {
        return "Registrarse";
    }

    /**
     * Procesa el registro de un nuevo usuario
     * @param usuario Datos del usuario a registrar
     * @param redirectAttributes Para mostrar mensajes
     * @return Redirección a login o registro según resultado
     */
    @PostMapping("/registrarse")
    public String registrarUsuario(
            @RequestParam String userName,
            @RequestParam String password,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam String rol,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validar si el usuario ya existe
            if (usuarioRepository.findByUserName(userName).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está registrado");
                return "redirect:/registrarse";
            }
            
            // Validar si el email ya existe
            if (usuarioRepository.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado");
                return "redirect:/registrarse";
            }
            
            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUserName(userName);
            nuevoUsuario.setPassword(passwordEncoder.encode(password));
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setTelefono(telefono);
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setActivo(true);
            
            usuarioRepository.save(nuevoUsuario);
            
            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado exitosamente. Por favor inicia sesión.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "redirect:/registrarse";
        }
    }
}