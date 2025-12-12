package com.proyecto.SafeRoute.controller;

import com.proyecto.SafeRoute.service.ReporteService;
import com.proyecto.SafeRoute.service.DashboardService;
import com.proyecto.SafeRoute.service.EstudianteService;
import com.proyecto.SafeRoute.model.dto.ReporteDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;
    
    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private EstudianteService estudianteService;

    /**
     * Página principal de reportes con filtros
     * Ruta: /reportes
     * Vista: Resportes.html (nota la 's')
     */
    @GetMapping
    public String reportes(
            Model model,
            Authentication authentication,
            @RequestParam(required = false) Long rutaId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Long conductorId,
            @RequestParam(required = false, defaultValue = "VIAJES") String tipoReporte
    ) {
        try {
            // Usuario autenticado
            if (authentication != null) {
                String username = authentication.getName();
                model.addAttribute("usuario", dashboardService.obtenerUsuarioPorEmail(username));
            }

            // Cargar listas para los filtros con datos de ejemplo
            model.addAttribute("rutas", obtenerRutasEjemplo());
            model.addAttribute("conductores", obtenerConductoresEjemplo());

            // Mantener valores de los filtros en el formulario
            model.addAttribute("rutaId", rutaId);
            model.addAttribute("estado", estado);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);
            model.addAttribute("conductorId", conductorId);
            model.addAttribute("tipoReporte", tipoReporte);

            // Estadísticas generales
            model.addAttribute("totalViajes", reporteService.contarTotalViajes());
            model.addAttribute("promedioPuntualidad", reporteService.calcularPromedioPuntualidad());
            model.addAttribute("totalEstudiantes", estudianteService.contarEstudiantesActivos());
            model.addAttribute("distanciaTotal", reporteService.calcularDistanciaTotal());

            // Buscar reportes (siempre muestra datos, con o sin filtros)
            List<ReporteDTO> reportes = reporteService.buscarReportes(
                    rutaId, estado, fechaDesde, fechaHasta, conductorId, tipoReporte
            );
            model.addAttribute("reportes", reportes);

            return "Resportes"; // ✅ CORREGIDO: debe coincidir con Resportes.html

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los reportes: " + e.getMessage());
            return "Error"; // ✅ CORREGIDO: debe coincidir con Error.html
        }
    }

    /**
     * Generar y descargar PDF del reporte
     * Ruta: /reportes/pdf
     */
    @GetMapping("/pdf")
    public void generarPDF(
            @RequestParam(required = false) Long rutaId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Long conductorId,
            @RequestParam(required = false, defaultValue = "VIAJES") String tipoReporte,
            HttpServletResponse response
    ) {
        try {
            // Buscar datos con los filtros aplicados
            List<ReporteDTO> reportes = reporteService.buscarReportes(
                    rutaId, estado, fechaDesde, fechaHasta, conductorId, tipoReporte
            );

            // Generar PDF usando el PdfGenerator existente
            reporteService.generarPDF(reportes, fechaDesde, fechaHasta, response);

        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, intentar enviar un error al response
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Error al generar el PDF: " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    // ===== MÉTODOS AUXILIARES PARA DATOS DE EJEMPLO =====
    
    private List<RutaEjemplo> obtenerRutasEjemplo() {
        List<RutaEjemplo> rutas = new ArrayList<>();
        
        rutas.add(new RutaEjemplo(1L, "Ruta Norte - Zona 1"));
        rutas.add(new RutaEjemplo(2L, "Ruta Sur - Zona 2"));
        rutas.add(new RutaEjemplo(3L, "Ruta Este - Zona 3"));
        rutas.add(new RutaEjemplo(4L, "Ruta Oeste - Zona 4"));
        rutas.add(new RutaEjemplo(5L, "Ruta Centro - Principal"));
        
        return rutas;
    }
    
    private List<ConductorEjemplo> obtenerConductoresEjemplo() {
        List<ConductorEjemplo> conductores = new ArrayList<>();
        
        conductores.add(new ConductorEjemplo(1L, "Carlos Méndez"));
        conductores.add(new ConductorEjemplo(2L, "Ana García"));
        conductores.add(new ConductorEjemplo(3L, "Roberto Silva"));
        conductores.add(new ConductorEjemplo(4L, "María López"));
        
        return conductores;
    }

    // ===== CLASES INTERNAS PARA DATOS DE EJEMPLO =====
    
    public static class RutaEjemplo {
        private Long id;
        private String nombre;
        
        public RutaEjemplo(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        
        public Long getId() { return id; }
        public String getNombre() { return nombre; }
    }
    
    public static class ConductorEjemplo {
        private Long id;
        private String nombre;
        
        public ConductorEjemplo(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        
        public Long getId() { return id; }
        public String getNombre() { return nombre; }
    }
}