package com.proyecto.SafeRoute.service;

import com.proyecto.SafeRoute.model.RutaEstadisticas;
import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import com.proyecto.SafeRoute.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Servicio para dashboard y métricas del sistema
 * Proporciona datos agregados para visualización
 */
@Service
@Transactional
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RutaRepository rutaRepository;

    /**
     * Obtener usuario por email o username
     */
    public Usuario obtenerUsuarioPorEmail(String username) {
        return usuarioRepository.findByUserName(username)
                .or(() -> usuarioRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Contar alertas pendientes
     * Implementación temporal hasta crear modelo Alertas
     */
    public long contarAlertasPendientes() {
        return 7L; // Pendiente implementación modelo Alertas
    }

    /**
     * Obtener actividad reciente del sistema
     * Datos simulados hasta crear modelo Actividad
     */
    public List<Map<String, String>> obtenerActividadReciente(int limite) {
        List<Map<String, String>> actividades = new ArrayList<>();
        
        String[] descripciones = {
            "Nuevo estudiante registrado",
            "Pago confirmado - Ruta Norte", 
            "Ruta modificada - Ruta Sur",
            "Conductor conectado - Carlos R.",
            "Estudiante desactivado - Juan P.",
            "Ruta completada exitosamente",
            "Nuevo conductor registrado"
        };
        
        String[] tipos = {"primary", "success", "warning", "info", "danger", "success", "info"};
        String[] tiempos = {"Hace 5 minutos", "Hace 15 minutos", "Hace 30 minutos", 
                          "Hace 1 hora", "Hace 2 horas", "Hace 3 horas", "Hace 4 horas"};
        
        for (int i = 0; i < Math.min(limite, descripciones.length); i++) {
            Map<String, String> actividad = new HashMap<>();
            actividad.put("descripcion", descripciones[i]);
            actividad.put("tiempo", tiempos[i]);
            actividad.put("tipo", tipos[i]);
            actividades.add(actividad);
        }
        
        return actividades;
    }

    /**
     * Obtener distribución de estudiantes por ruta para el gráfico
     * Usa RutaEstadisticas del RutaRepository
     */
    public List<Map<String, Object>> obtenerDistribucionEstudiantesPorRuta() {
        List<RutaEstadisticas> estadisticas = rutaRepository.obtenerDistribucionEstudiantes();
        List<Map<String, Object>> distribucion = new ArrayList<>();
        
        for (RutaEstadisticas stats : estadisticas) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombreRuta", stats.getNombre());
            item.put("cantidadEstudiantes", stats.getEstudiantesActuales());
            item.put("capacidadMaxima", stats.getCapacidadMaxima());
            item.put("porcentajeOcupacion", 
                stats.getCapacidadMaxima() > 0 ? 
                (double) stats.getEstudiantesActuales() / stats.getCapacidadMaxima() * 100 : 0);
            distribucion.add(item);
        }
        
        return distribucion;
    }

    /**
     * Contar viajes realizados hoy por un conductor
     * Implementación temporal hasta crear modelo Viaje
     */
    public long contarViajesHoy(Long conductorId) {
        return 2L; // Pendiente implementación modelo Viaje
    }
}
