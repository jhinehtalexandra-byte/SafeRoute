package com.proyecto.SafeRoute.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad Usuario para el sistema RutaEscolar
 * Representa a todos los usuarios del sistema (Admin, Padre, Conductor)
 */
@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 20)
    private String rol; // ADMIN, PADRE, CONDUCTOR

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Obtiene el rol con el prefijo ROLE_ requerido por Spring Security
     * @return Rol con prefijo ROLE_
     */
    public String getRolWithPrefix() {
        return "ROLE_" + this.rol;
    }
}