package com.proyecto.SafeRoute.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para Spring Security
 * Define rutas públicas, privadas y permisos por rol
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configuración principal de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (sin autenticación)
                .requestMatchers("/", "/home", "/login", "/registrarse", "/error", "/logout").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**").permitAll()
                
                // Rutas para ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/reportes/**").hasRole("ADMIN")
                .requestMatchers("/estudiantes/**").hasRole("ADMIN")
                
                // Rutas para PADRE
                .requestMatchers("/padre/**").hasRole("PADRE")
                
                // Rutas para CONDUCTOR
                .requestMatchers("/conductor/**").hasRole("CONDUCTOR")
                .requestMatchers("/mapa/**").hasRole("CONDUCTOR")
                
                // Rutas compartidas (ADMIN y PADRE)
                .requestMatchers("/pagos/**").hasAnyRole("PADRE", "ADMIN")
                
                // Rutas compartidas (requieren autenticación - todos los roles)
                .requestMatchers("/perfil/**").authenticated()
                .requestMatchers("/dashboard").authenticated()
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error")
            )
            .csrf(csrf -> csrf.disable()); // Deshabilitado para desarrollo

        return http.build();
    }

    /**
     * Encoder para encriptar contraseñas con BCrypt
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Manager de autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}