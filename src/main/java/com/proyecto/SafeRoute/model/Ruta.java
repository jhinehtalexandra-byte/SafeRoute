package com.proyecto.SafeRoute.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Ruta
 * Representa las rutas de transporte escolar
 */
@Entity
@Table(name = "rutas")
@Data
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo; // Ejemplo: RUTA-NORTE-01

    @Column(nullable = false, length = 100)
    private String nombre; // Ejemplo: Ruta Norte

    @Column(length = 500)
    private String descripcion;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio; // Hora de salida

    @Column(name = "hora_fin")
    private LocalTime horaFin; // Hora estimada de llegada

    @Column(length = 20)
    private String turno; // MAÑANA, TARDE

    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    @Column(nullable = false)
    private Boolean activo = true;

    // Relación con Conductor (Usuario con rol CONDUCTOR)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conductor_id")
    private Usuario conductor;

    // Relación con Estudiantes (una ruta tiene muchos estudiantes)
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL)
    private List<Estudiante> estudiantes = new ArrayList<>();

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
     * Obtiene el número de estudiantes en la ruta
     */
    public int getCantidadEstudiantes() {
        return estudiantes != null ? estudiantes.size() : 0;
    }

    /**
     * Verifica si la ruta está llena
     */
    public boolean estaLlena() {
        if (capacidadMaxima == null) return false;
        return getCantidadEstudiantes() >= capacidadMaxima;
    }

    /**
     * Obtiene el porcentaje de ocupación
     */
    public double getPorcentajeOcupacion() {
        if (capacidadMaxima == null || capacidadMaxima == 0) return 0;
        return (getCantidadEstudiantes() * 100.0) / capacidadMaxima;
    }
}