package com.proyecto.SafeRoute.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Páginas públicas (sin autenticación)
                .requestMatchers("/", "/home", "/login", "/css/**", "/js/**", "/img/**", "/images/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // Si usas H2
                
                // Dashboard solo para ADMIN
                .requestMatchers("/dashboard", "/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/Users", "/Users/**").hasRole("ADMIN")
                .requestMatchers("/estudiantes", "/estudiantes/**").hasRole("ADMIN")
                .requestMatchers("/rutas", "/rutas/**").hasRole("ADMIN")
                .requestMatchers("/alertas", "/alertas/**").hasRole("ADMIN")
                .requestMatchers("/control-pagos", "/control-pagos/**").hasRole("ADMIN")
                .requestMatchers("/tracking", "/tracking/**").hasRole("ADMIN")
                .requestMatchers("/reportes", "/reportes/**").hasRole("ADMIN")
                .requestMatchers("/facturacion", "/facturacion/**").hasRole("ADMIN")
                .requestMatchers("/form", "/form/**").hasRole("ADMIN")
                
                // Páginas para DRIVER
                .requestMatchers("/DriverMap", "/DriverMap/**").hasRole("DRIVER")
                
                // Páginas para PARENT
                .requestMatchers("/Payments", "/Payments/**").hasRole("PARENT")
                
                // Perfil accesible para todos los usuarios autenticados
                .requestMatchers("/Profile", "/Profile/**").authenticated()
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/auth-success", false)  // Ir a un controlador que redirige según rol
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home?logout")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/login?denied");
                })
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}