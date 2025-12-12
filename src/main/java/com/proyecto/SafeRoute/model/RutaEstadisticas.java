package com.proyecto.SafeRoute.model;

public class RutaEstadisticas {
    private Long id;
    private String nombre;
    private long estudiantesActuales;
    private int capacidadMaxima;
    
    public RutaEstadisticas(Long id, String nombre, long estudiantesActuales, int capacidadMaxima) {
        this.id = id;
        this.nombre = nombre;
        this.estudiantesActuales = estudiantesActuales;
        this.capacidadMaxima = capacidadMaxima;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public long getEstudiantesActuales() { return estudiantesActuales; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
}
