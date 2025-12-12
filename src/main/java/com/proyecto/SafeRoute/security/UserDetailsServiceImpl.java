package com.proyecto.SafeRoute.security;

import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementación de UserDetailsService para Spring Security
 * Carga los detalles del usuario desde la base de datos
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario por su nombre de usuario
     * @param username Nombre de usuario
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Validar si el usuario está activo
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        return new User(
                usuario.getUserName(),
                usuario.getPassword(),
                usuario.getActivo(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                getAuthorities(usuario)
        );
    }

    /**
     * Convierte el rol del usuario en autoridades de Spring Security
     * @param usuario Usuario del sistema
     * @return Colección de autoridades (roles)
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        // El rol debe tener el prefijo ROLE_ para Spring Security
        String rol = usuario.getRol().startsWith("ROLE_") 
                ? usuario.getRol() 
                : "ROLE_" + usuario.getRol();
        
        return Collections.singletonList(new SimpleGrantedAuthority(rol));
    }
}