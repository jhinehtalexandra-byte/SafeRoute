package com.proyecto.SafeRoute.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Estudiante
 * Representa a los estudiantes que usan el servicio de transporte
 */
@Entity
@Table(name = "estudiantes")
@Data
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(unique = true, nullable = false, length = 20)
    private String documento; // Cédula o documento de identidad

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 200)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String grado; // Grado escolar

    @Column(length = 100)
    private String institucion; // Nombre del colegio

    @Column(nullable = false)
    private Boolean activo = true;

    // Relación con Padre (Usuario con rol PADRE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id")
    private Usuario padre;

    // Relación con Ruta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Obtiene el nombre completo del estudiante
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Calcula la edad del estudiante
     */
    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}