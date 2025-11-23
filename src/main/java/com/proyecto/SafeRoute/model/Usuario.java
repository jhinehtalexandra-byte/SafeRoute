package com.proyecto.SafeRoute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false, length = 100)
    private String userName;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String rol; // Ej: ADMIN, PARENT, DRIVER

    // ========== Información personal (común para todos) ==========
    
    @Column(name = "nombre_completo", length = 200)
    private String nombreCompleto;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 50)
    private String cedula;

    @Column(length = 300)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // ========== Información específica para DRIVER (conductores) ==========
    
    @Column(name = "licencia_conducir", length = 50)
    private String licenciaConducir;

    @Column(name = "fecha_vencimiento_licencia")
    private LocalDate fechaVencimientoLicencia;

    @Column(name = "tipo_licencia", length = 20)
    private String tipoLicencia;

    @Column(name = "placa_vehiculo", length = 20)
    private String placaVehiculo;

    // ========== Información específica para PARENT (padres) ==========
    
    @Column(name = "nombre_estudiante", length = 200)
    private String nombreEstudiante;

    @Column(name = "grado_estudiante", length = 50)
    private String gradoEstudiante;

    @Column(name = "nombre_contacto_emergencia", length = 200)
    private String nombreContactoEmergencia;

    @Column(name = "telefono_emergencia", length = 20)
    private String telefonoEmergencia;

    // ========== Metadatos del sistema ==========
    
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @Column(nullable = false)
    private Boolean activo = true;

    // ========== Métodos de ciclo de vida JPA ==========
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        ultimaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }
}