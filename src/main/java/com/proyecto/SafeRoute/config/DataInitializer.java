package com.proyecto.SafeRoute.config;

import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Inicializador de datos de la base de datos
 * Crea usuarios por defecto al iniciar la aplicación SOLO si no existen
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository repo) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            
            // Lista de usuarios esperados (solo crea si NO existen)
            String[][] usuariosEsperados = {
                {"admin", "ADMIN", "Administrador Sistema", "admin@saferoute.com", "+57-300-0000001"},
                {"padre1", "PADRE", "Juan Rodríguez", "juan.rodriguez@email.com", "+57-300-5555555"},
                {"conductor1", "CONDUCTOR", "Carlos Méndez", "carlos.mendez@saferoute.com", "+57-300-1111111"}
            };
            
            // Crear usuarios solo si no existen
            for (String[] usuario : usuariosEsperados) {
                if (repo.findByUserName(usuario[0]).isEmpty()) {
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setUserName(usuario[0]);
                    nuevoUsuario.setPassword(encoder.encode("123"));  // Siempre "123"
                    nuevoUsuario.setRol(usuario[1]);
                    nuevoUsuario.setNombre(usuario[2]);
                    nuevoUsuario.setEmail(usuario[3]);
                    nuevoUsuario.setTelefono(usuario[4]);
                    nuevoUsuario.setActivo(true);
                    repo.save(nuevoUsuario);
                    System.out.println("✅ Usuario " + usuario[0] + " (" + usuario[1] + ") creado exitosamente");
                } else {
                    System.out.println("ℹ️ Usuario " + usuario[0] + " ya existe en BD");
                }
            }
            
            // MOSTRAR TODOS los usuarios de la base de datos (SQL + DataInitializer)
            System.out.println("\n🚀 Inicialización completada - USUARIOS EN BASE DE DATOS:");
            System.out.println("═══════════════════════════════════════════════");
            repo.findAll().forEach(usuario -> 
                System.out.println("   • " + usuario.getUserName() + " / 123 (Rol: " + 
                                 usuario.getRol() + ") - " + usuario.getNombre())
            );
            System.out.println("═══════════════════════════════════════════════\n");
        };
    }
}
