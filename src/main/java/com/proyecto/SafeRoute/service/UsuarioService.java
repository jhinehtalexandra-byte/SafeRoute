package com.proyecto.SafeRoute.service;

import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios
 * Implementa la lógica de negocio relacionada con usuarios y autenticación
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<Usuario> obtenerPorId(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        return usuarioRepository.findById(id);
    }

    /**
     * Obtiene un usuario por username
     */
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUserName(username);
    }

    /**
     * Obtiene un usuario por email
     */
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> obtenerPorRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Obtiene conductores activos
     */
    public List<Usuario> obtenerConductoresActivos() {
        return usuarioRepository.findByRolAndActivo("CONDUCTOR", true);
    }

    /**
     * Obtiene padres activos
     */
    public List<Usuario> obtenerPadresActivos() {
        return usuarioRepository.findByRolAndActivo("PADRE", true);
    }

    /**
     * Verifica si existe username
     */
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUserName(username);
    }

    /**
     * Verifica si existe email
     */
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Crea un nuevo usuario
     */
    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByUserName(usuario.getUserName())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso");
        }

        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            throw new RuntimeException("La contraseña es requerida");
        }

        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza un usuario existente
     */
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuarioExistente = usuarioOpt.get();

        if (!usuarioExistente.getUserName().equals(usuarioActualizado.getUserName())) {
            if (usuarioRepository.existsByUserName(usuarioActualizado.getUserName())) {
                throw new RuntimeException("El nombre de usuario ya está en uso");
            }
            usuarioExistente.setUserName(usuarioActualizado.getUserName());
        }

        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
                throw new RuntimeException("El correo electrónico ya está en uso");
            }
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
        }

        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setRol(usuarioActualizado.getRol());
        usuarioExistente.setActivo(usuarioActualizado.getActivo());

        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Actualiza la contraseña de un usuario
     */
    public void actualizarPassword(Long id, String nuevaPassword) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    /**
     * Activa o desactiva un usuario
     */
    public Usuario toggleEstado(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setActivo(!usuario.getActivo());
        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario
     */
    public void eliminar(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Cuenta usuarios por rol
     */
    public long contarPorRol(String rol) {
        return usuarioRepository.countByRol(rol);
    }

    /**
     * Cuenta conductores activos
     */
    public long contarConductoresActivos() {
        return usuarioRepository.countByRolAndActivo("CONDUCTOR", true);
    }

    /**
     * Busca usuarios por nombre
     */
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Valida credenciales de usuario
     */
    public boolean validarCredenciales(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUserName(username);
        
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        return passwordEncoder.matches(password, usuario.getPassword()) && usuario.getActivo();
    }

    /**
     * Registra un nuevo padre
     */
    public Usuario registrarPadre(Usuario usuario) {
        usuario.setRol("PADRE");
        usuario.setActivo(true);
        return crear(usuario);
    }
}
