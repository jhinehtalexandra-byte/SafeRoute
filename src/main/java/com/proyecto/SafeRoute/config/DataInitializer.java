package com.proyecto.SafeRoute.config;

import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository repo, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            // Usuario ADMIN
            Optional<Usuario> adminOpt = repo.findByUserName("admin");
            if (adminOpt.isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUserName("admin");
                admin.setPassword(passwordEncoder.encode("123"));
                admin.setRol("ADMIN");
                repo.save(admin);
                System.out.println("✅ Usuario admin creado con éxito");
            } else {
                System.out.println("ℹ️ Usuario admin ya existe");
            }

            // Usuario PARENT (Padre)
            Optional<Usuario> padreOpt = repo.findByUserName("padre1");
            if (padreOpt.isEmpty()) {
                Usuario padre = new Usuario();
                padre.setUserName("padre1");
                padre.setPassword(passwordEncoder.encode("123"));
                padre.setRol("PARENT");
                repo.save(padre);
                System.out.println("✅ Usuario padre1 creado con éxito");
            } else {
                System.out.println("ℹ️ Usuario padre1 ya existe");
            }

            // Usuario DRIVER (Conductor)
            Optional<Usuario> conductorOpt = repo.findByUserName("conductor1");
            if (conductorOpt.isEmpty()) {
                Usuario conductor = new Usuario();
                conductor.setUserName("conductor1");
                conductor.setPassword(passwordEncoder.encode("123"));
                conductor.setRol("DRIVER");
                repo.save(conductor);
                System.out.println("✅ Usuario conductor1 creado con éxito");
            } else {
                System.out.println("ℹ️ Usuario conductor1 ya existe");
            }
        };
    }
}