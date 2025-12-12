package com.proyecto.SafeRoute.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReporteDTO {
    private LocalDate fecha;
    private Long rutaId;
    private String rutaNombre;
    private Long conductorId;
    private String conductorNombre;
    private String horaInicio;
    private String horaFin;
    private String duracion;
    private Integer cantidadEstudiantes;
    private String estado; // A_TIEMPO, RETRASADO, COMPLETADO, CANCELADO
    private String estadoTexto;
    private Double distancia;
}