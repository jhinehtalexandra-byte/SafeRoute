package com.proyecto.SafeRoute.service;

import com.proyecto.SafeRoute.model.dto.ReporteDTO;
import com.proyecto.SafeRoute.utils.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para generación de reportes y estadísticas
 * Proporciona datos para ReporteController y PDF
 */
@Service
@Transactional
public class ReporteService {

    @Autowired
    private PdfGenerator pdfGenerator;

    /**
     * Buscar reportes con filtros multicriterio
     */
    public List<ReporteDTO> buscarReportes(Long rutaId, String estado, LocalDate fechaDesde, 
                                          LocalDate fechaHasta, Long conductorId, String tipoReporte) {
        
        // Generar datos de ejemplo realistas
        List<ReporteDTO> reportes = generarDatosEjemplo();
        
        // Aplicar filtros
        if (rutaId != null) {
            reportes = reportes.stream()
                    .filter(r -> r.getRutaId().equals(rutaId))
                    .collect(Collectors.toList());
        }
        
        if (estado != null && !estado.isEmpty()) {
            reportes = reportes.stream()
                    .filter(r -> r.getEstado().equals(estado))
                    .collect(Collectors.toList());
        }
        
        if (fechaDesde != null) {
            reportes = reportes.stream()
                    .filter(r -> !r.getFecha().isBefore(fechaDesde))
                    .collect(Collectors.toList());
        }
        
        if (fechaHasta != null) {
            reportes = reportes.stream()
                    .filter(r -> !r.getFecha().isAfter(fechaHasta))
                    .collect(Collectors.toList());
        }
        
        if (conductorId != null) {
            reportes = reportes.stream()
                    .filter(r -> r.getConductorId().equals(conductorId))
                    .collect(Collectors.toList());
        }
        
        return reportes;
    }

    /**
     * Genera datos de ejemplo para el reporte
     */
    private List<ReporteDTO> generarDatosEjemplo() {
        List<ReporteDTO> reportes = new ArrayList<>();
        
        // Reporte 1 - Ruta 1
        ReporteDTO reporte1 = new ReporteDTO();
        reporte1.setFecha(LocalDate.now());
        reporte1.setRutaId(1L);
        reporte1.setRutaNombre("Ruta Norte - Zona 1");
        reporte1.setConductorId(1L);
        reporte1.setConductorNombre("Carlos Méndez");
        reporte1.setHoraInicio("06:30");
        reporte1.setHoraFin("07:42");
        reporte1.setDuracion("1h 12min");
        reporte1.setCantidadEstudiantes(25);
        reporte1.setEstado("A_TIEMPO");
        reporte1.setEstadoTexto("A tiempo");
        reporte1.setDistancia(15.3);
        reportes.add(reporte1);
        
        // Reporte 2 - Ruta 2
        ReporteDTO reporte2 = new ReporteDTO();
        reporte2.setFecha(LocalDate.now());
        reporte2.setRutaId(2L);
        reporte2.setRutaNombre("Ruta Sur - Zona 2");
        reporte2.setConductorId(2L);
        reporte2.setConductorNombre("Ana García");
        reporte2.setHoraInicio("07:00");
        reporte2.setHoraFin("08:25");
        reporte2.setDuracion("1h 25min");
        reporte2.setCantidadEstudiantes(18);
        reporte2.setEstado("RETRASADO");
        reporte2.setEstadoTexto("Retrasado");
        reporte2.setDistancia(22.5);
        reportes.add(reporte2);
        
        // Reporte 3 - Ruta 3
        ReporteDTO reporte3 = new ReporteDTO();
        reporte3.setFecha(LocalDate.now());
        reporte3.setRutaId(3L);
        reporte3.setRutaNombre("Ruta Este - Zona 3");
        reporte3.setConductorId(3L);
        reporte3.setConductorNombre("Roberto Silva");
        reporte3.setHoraInicio("06:45");
        reporte3.setHoraFin("08:05");
        reporte3.setDuracion("1h 20min");
        reporte3.setCantidadEstudiantes(22);
        reporte3.setEstado("A_TIEMPO");
        reporte3.setEstadoTexto("A tiempo");
        reporte3.setDistancia(18.7);
        reportes.add(reporte3);
        
        // Reporte 4 - Ruta 4
        ReporteDTO reporte4 = new ReporteDTO();
        reporte4.setFecha(LocalDate.now().minusDays(1));
        reporte4.setRutaId(4L);
        reporte4.setRutaNombre("Ruta Oeste - Zona 4");
        reporte4.setConductorId(4L);
        reporte4.setConductorNombre("María López");
        reporte4.setHoraInicio("07:15");
        reporte4.setHoraFin("08:30");
        reporte4.setDuracion("1h 15min");
        reporte4.setCantidadEstudiantes(15);
        reporte4.setEstado("A_TIEMPO");
        reporte4.setEstadoTexto("A tiempo");
        reporte4.setDistancia(12.8);
        reportes.add(reporte4);
        
        // Reporte 5 - Ruta Centro
        ReporteDTO reporte5 = new ReporteDTO();
        reporte5.setFecha(LocalDate.now().minusDays(1));
        reporte5.setRutaId(5L);
        reporte5.setRutaNombre("Ruta Centro - Principal");
        reporte5.setConductorId(1L);
        reporte5.setConductorNombre("Carlos Méndez");
        reporte5.setHoraInicio("13:30");
        reporte5.setHoraFin("14:45");
        reporte5.setDuracion("1h 15min");
        reporte5.setCantidadEstudiantes(20);
        reporte5.setEstado("COMPLETADO");
        reporte5.setEstadoTexto("Completado");
        reporte5.setDistancia(14.2);
        reportes.add(reporte5);
        
        // Reporte 6
        ReporteDTO reporte6 = new ReporteDTO();
        reporte6.setFecha(LocalDate.now().minusDays(2));
        reporte6.setRutaId(1L);
        reporte6.setRutaNombre("Ruta Norte - Zona 1");
        reporte6.setConductorId(1L);
        reporte6.setConductorNombre("Carlos Méndez");
        reporte6.setHoraInicio("06:30");
        reporte6.setHoraFin("07:50");
        reporte6.setDuracion("1h 20min");
        reporte6.setCantidadEstudiantes(24);
        reporte6.setEstado("RETRASADO");
        reporte6.setEstadoTexto("Retrasado");
        reporte6.setDistancia(15.3);
        reportes.add(reporte6);
        
        // Reporte 7
        ReporteDTO reporte7 = new ReporteDTO();
        reporte7.setFecha(LocalDate.now().minusDays(2));
        reporte7.setRutaId(2L);
        reporte7.setRutaNombre("Ruta Sur - Zona 2");
        reporte7.setConductorId(2L);
        reporte7.setConductorNombre("Ana García");
        reporte7.setHoraInicio("07:00");
        reporte7.setHoraFin("08:15");
        reporte7.setDuracion("1h 15min");
        reporte7.setCantidadEstudiantes(19);
        reporte7.setEstado("A_TIEMPO");
        reporte7.setEstadoTexto("A tiempo");
        reporte7.setDistancia(22.5);
        reportes.add(reporte7);
        
        // Reporte 8
        ReporteDTO reporte8 = new ReporteDTO();
        reporte8.setFecha(LocalDate.now().minusDays(3));
        reporte8.setRutaId(3L);
        reporte8.setRutaNombre("Ruta Este - Zona 3");
        reporte8.setConductorId(3L);
        reporte8.setConductorNombre("Roberto Silva");
        reporte8.setHoraInicio("06:45");
        reporte8.setHoraFin("08:00");
        reporte8.setDuracion("1h 15min");
        reporte8.setCantidadEstudiantes(21);
        reporte8.setEstado("A_TIEMPO");
        reporte8.setEstadoTexto("A tiempo");
        reporte8.setDistancia(18.7);
        reportes.add(reporte8);
        
        // Reporte 9
        ReporteDTO reporte9 = new ReporteDTO();
        reporte9.setFecha(LocalDate.now().minusDays(3));
        reporte9.setRutaId(4L);
        reporte9.setRutaNombre("Ruta Oeste - Zona 4");
        reporte9.setConductorId(4L);
        reporte9.setConductorNombre("María López");
        reporte9.setHoraInicio("07:15");
        reporte9.setHoraFin("08:35");
        reporte9.setDuracion("1h 20min");
        reporte9.setCantidadEstudiantes(16);
        reporte9.setEstado("RETRASADO");
        reporte9.setEstadoTexto("Retrasado");
        reporte9.setDistancia(12.8);
        reportes.add(reporte9);
        
        // Reporte 10
        ReporteDTO reporte10 = new ReporteDTO();
        reporte10.setFecha(LocalDate.now().minusDays(4));
        reporte10.setRutaId(5L);
        reporte10.setRutaNombre("Ruta Centro - Principal");
        reporte10.setConductorId(1L);
        reporte10.setConductorNombre("Carlos Méndez");
        reporte10.setHoraInicio("13:30");
        reporte10.setHoraFin("14:40");
        reporte10.setDuracion("1h 10min");
        reporte10.setCantidadEstudiantes(21);
        reporte10.setEstado("A_TIEMPO");
        reporte10.setEstadoTexto("A tiempo");
        reporte10.setDistancia(14.2);
        reportes.add(reporte10);
        
        // Reporte 11 - Cancelado
        ReporteDTO reporte11 = new ReporteDTO();
        reporte11.setFecha(LocalDate.now().minusDays(5));
        reporte11.setRutaId(2L);
        reporte11.setRutaNombre("Ruta Sur - Zona 2");
        reporte11.setConductorId(2L);
        reporte11.setConductorNombre("Ana García");
        reporte11.setHoraInicio("07:00");
        reporte11.setHoraFin("-");
        reporte11.setDuracion("-");
        reporte11.setCantidadEstudiantes(0);
        reporte11.setEstado("CANCELADO");
        reporte11.setEstadoTexto("Cancelado");
        reporte11.setDistancia(0.0);
        reportes.add(reporte11);
        
        // Reporte 12
        ReporteDTO reporte12 = new ReporteDTO();
        reporte12.setFecha(LocalDate.now().minusDays(5));
        reporte12.setRutaId(1L);
        reporte12.setRutaNombre("Ruta Norte - Zona 1");
        reporte12.setConductorId(1L);
        reporte12.setConductorNombre("Carlos Méndez");
        reporte12.setHoraInicio("06:30");
        reporte12.setHoraFin("07:45");
        reporte12.setDuracion("1h 15min");
        reporte12.setCantidadEstudiantes(23);
        reporte12.setEstado("A_TIEMPO");
        reporte12.setEstadoTexto("A tiempo");
        reporte12.setDistancia(15.3);
        reportes.add(reporte12);
        
        return reportes;
    }

    /**
     * Contar total de viajes
     */
    public long contarTotalViajes() {
        return 1248L;
    }

    /**
     * Calcular promedio de puntualidad
     */
    public String calcularPromedioPuntualidad() {
        return "90%";
    }

    /**
     * Calcular distancia total recorrida
     */
    public String calcularDistanciaTotal() {
        return "12,450 km";
    }

    /**
     * Generar PDF del reporte usando PdfGenerator
     */
    public void generarPDF(List<ReporteDTO> reportes, LocalDate fechaDesde, LocalDate fechaHasta, 
                          HttpServletResponse response) throws Exception {
        // Usar el PdfGenerator existente
        pdfGenerator.generarPdf("reporte-viajes", reportes, fechaDesde, fechaHasta, response);
    }
}
