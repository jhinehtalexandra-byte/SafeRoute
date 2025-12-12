package com.proyecto.SafeRoute.repository;

import com.proyecto.SafeRoute.model.Ruta;
import com.proyecto.SafeRoute.model.RutaEstadisticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Ruta
 * Proporciona métodos de acceso a datos de rutas
 */
@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    
    /**
     * Busca una ruta por código
     */
    Optional<Ruta> findByCodigo(String codigo);
    
    /**
     * Busca rutas por código (búsqueda parcial, case insensitive)
     */
    List<Ruta> findByCodigoContainingIgnoreCase(String codigo);
    
    /**
     * Busca rutas por nombre (búsqueda parcial, case insensitive)
     */
    List<Ruta> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca rutas por turno
     */
    List<Ruta> findByTurno(String turno);
    
    /**
     * Busca rutas por estado (activo/inactivo)
     */
    List<Ruta> findByActivo(boolean activo);
    
    /**
     * Busca rutas por conductor
     */
    List<Ruta> findByConductorId(Long conductorId);
    
    /**
     * Verifica si existe una ruta con un código específico
     */
    boolean existsByCodigo(String codigo);
    
    /**
     * Cuenta rutas por estado
     */
    long countByActivo(boolean activo);
    
    /**
     * Cuenta rutas por turno
     */
    long countByTurno(String turno);

    /**
     * MÉTODO PARA DASHBOARD SERVICE
     * Obtiene distribución de estudiantes por ruta para dashboard
     */
    @Query("SELECT new com.proyecto.SafeRoute.model.RutaEstadisticas(" +
           "r.id, r.nombre, COUNT(e.id), r.capacidadMaxima) " +
           "FROM Ruta r LEFT JOIN r.estudiantes e " +
           "WHERE r.activo = true " +
           "GROUP BY r.id, r.nombre, r.capacidadMaxima")
    List<RutaEstadisticas> obtenerDistribucionEstudiantes();
}
