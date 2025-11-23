package com.proyecto.SafeRoute.controller;

import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ========== PÁGINA DE INICIO (PÚBLICA, SIN AUTENTICACIÓN) ==========
    
    @GetMapping("/")
    public String index() {
        return "home";  // Landing page pública
    }

    @GetMapping("/home")
    public String home() {
        return "home";  // Landing page pública
    }

    // ========== PÁGINA DE LOGIN ==========
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ========== DASHBOARD (solo para ADMIN después del login) ==========
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = auth.getName();
        String roles = auth.getAuthorities().toString();
        
        model.addAttribute("username", username);
        model.addAttribute("rol", roles);
        
        return "dashboard";
    }

    // ========== PÁGINAS SOLO PARA ADMIN ==========
    
    @GetMapping("/Users")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", repo.findAll());
        return "Users";
    }

    @GetMapping("/Users/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form";
    }

    @PostMapping("/Users/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        repo.save(usuario);
        return "redirect:/Users";
    }

    @GetMapping("/Users/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        if (id == null) {
            throw new IllegalArgumentException("ID no puede ser nulo");
        }
        
        Usuario usuario = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        model.addAttribute("usuario", usuario);
        return "form";
    }

    @GetMapping("/Users/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID no puede ser nulo");
        }
        
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        
        repo.deleteById(id);
        return "redirect:/Users";
    }

    // ========== PÁGINA PARA PADRES (PARENT) ==========
    
    @GetMapping("/Payments")
    public String payments(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = auth.getName();
        model.addAttribute("username", username);
        
        return "Payments";
    }

    // ========== PÁGINA PARA CONDUCTORES (DRIVER) ==========
    
    @GetMapping("/DriverMap")
    public String driverMap(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = auth.getName();
        model.addAttribute("username", username);
        
        return "DriverMap";
    }

    // ========== PERFIL (todos los usuarios autenticados) ==========
    
    @GetMapping("/Profile")
    public String perfil(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = auth.getName();
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Usuario no autenticado correctamente");
        }
        
        Usuario usuario = repo.findByUserName(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
        
        model.addAttribute("usuario", usuario);
        return "Profile";
    }

    @PostMapping("/Profile/guardar")
    public String guardarPerfil(@ModelAttribute Usuario usuarioForm, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = auth.getName();
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Usuario no autenticado correctamente");
        }
        
        Usuario actual = repo.findByUserName(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
        
        // Actualizar información básica
        if (usuarioForm.getUserName() != null && !usuarioForm.getUserName().isEmpty()) {
            actual.setUserName(usuarioForm.getUserName());
        }
        
        // Actualizar password solo si se proporciona uno nuevo
        if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isEmpty()) {
            actual.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
        }
        
        // Actualizar información personal
        if (usuarioForm.getNombreCompleto() != null) {
            actual.setNombreCompleto(usuarioForm.getNombreCompleto());
        }
        if (usuarioForm.getEmail() != null) {
            actual.setEmail(usuarioForm.getEmail());
        }
        if (usuarioForm.getTelefono() != null) {
            actual.setTelefono(usuarioForm.getTelefono());
        }
        if (usuarioForm.getCedula() != null) {
            actual.setCedula(usuarioForm.getCedula());
        }
        if (usuarioForm.getDireccion() != null) {
            actual.setDireccion(usuarioForm.getDireccion());
        }
        if (usuarioForm.getCiudad() != null) {
            actual.setCiudad(usuarioForm.getCiudad());
        }
        if (usuarioForm.getFechaNacimiento() != null) {
            actual.setFechaNacimiento(usuarioForm.getFechaNacimiento());
        }
        
        // Campos específicos para DRIVER
        if ("DRIVER".equals(actual.getRol())) {
            if (usuarioForm.getLicenciaConducir() != null) {
                actual.setLicenciaConducir(usuarioForm.getLicenciaConducir());
            }
            if (usuarioForm.getFechaVencimientoLicencia() != null) {
                actual.setFechaVencimientoLicencia(usuarioForm.getFechaVencimientoLicencia());
            }
            if (usuarioForm.getTipoLicencia() != null) {
                actual.setTipoLicencia(usuarioForm.getTipoLicencia());
            }
            if (usuarioForm.getPlacaVehiculo() != null) {
                actual.setPlacaVehiculo(usuarioForm.getPlacaVehiculo());
            }
        }
        
        // Campos específicos para PARENT
        if ("PARENT".equals(actual.getRol())) {
            if (usuarioForm.getNombreEstudiante() != null) {
                actual.setNombreEstudiante(usuarioForm.getNombreEstudiante());
            }
            if (usuarioForm.getGradoEstudiante() != null) {
                actual.setGradoEstudiante(usuarioForm.getGradoEstudiante());
            }
            if (usuarioForm.getNombreContactoEmergencia() != null) {
                actual.setNombreContactoEmergencia(usuarioForm.getNombreContactoEmergencia());
            }
            if (usuarioForm.getTelefonoEmergencia() != null) {
                actual.setTelefonoEmergencia(usuarioForm.getTelefonoEmergencia());
            }
        }
        
        repo.save(actual);
        
        // Redirigir según el rol
        String rol = actual.getRol();
        if ("ADMIN".equals(rol)) {
            return "redirect:/dashboard?actualizado";
        } else if ("PARENT".equals(rol)) {
            return "redirect:/Profile?actualizado";
        } else if ("DRIVER".equals(rol)) {
            return "redirect:/Profile?actualizado";
        }
        
        return "redirect:/Profile?actualizado";
    }
}