package com.proyecto.SafeRoute.repository;

import com.proyecto.SafeRoute.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    /**
     * Buscar estudiantes por documento
     */
    Optional<Estudiante> findByDocumento(String documento);

    /**
     * Verificar si existe un documento
     */
    boolean existsByDocumento(String documento);

    /**
     * Buscar estudiantes activos
     */
    List<Estudiante> findByActivo(Boolean activo);

    // MÉTODOS FALTANTES PARA ESTUDIANTE SERVICE - CONVENCIONES AUTOMÁTICAS
    /**
     * Estudiantes por padre (todos)
     */
    List<Estudiante> findByPadreId(Long padreId);

    /**
     * Estudiantes activos por padre
     */
    List<Estudiante> findByPadreIdAndActivo(Long padreId, boolean activo);

    /**
     * Estudiantes por ruta (todos)
     */
    List<Estudiante> findByRutaId(Long rutaId);

    /**
     * Estudiantes activos por ruta
     */
    List<Estudiante> findByRutaIdAndActivo(Long rutaId, boolean activo);

    /**
     * Buscar por nombre o apellido (ignore case)
     */
    List<Estudiante> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido);

    /**
     * Contar estudiantes activos
     */
    long countByActivo(boolean activo);

    /**
     * Contar estudiantes por padre
     */
    long countByPadreId(Long padreId);

    /**
     * Contar estudiantes activos por ruta
     */
    long countByRutaIdAndActivo(Long rutaId, boolean activo);

    // MÉTODOS ORIGINALES (OPTIMIZADOS)
    /**
     * Buscar estudiantes por padre (activos) - versión original
     */
    @Query("SELECT e FROM Estudiante e WHERE e.padre.id = :padreId AND e.activo = true")
    List<Estudiante> findByPadreIdActivo(@Param("padreId") Long padreId);

    /**
     * Buscar estudiantes por ruta (activos) - versión original
     */
    @Query("SELECT e FROM Estudiante e WHERE e.ruta.id = :rutaId AND e.activo = true")
    List<Estudiante> findByRutaIdActivo(@Param("rutaId") Long rutaId);

    /**
     * Buscar estudiantes por conductor (a través de la ruta)
     */
    @Query("SELECT e FROM Estudiante e WHERE e.ruta.conductor.id = :conductorId AND e.activo = true")
    List<Estudiante> findByConductorId(@Param("conductorId") Long conductorId);

    /**
     * Contar estudiantes activos - versión original
     */
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.activo = true")
    long contarEstudiantesActivos();

    /**
     * Contar estudiantes por ruta - versión original
     */
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.ruta.id = :rutaId AND e.activo = true")
    long contarEstudiantesPorRuta(@Param("rutaId") Long rutaId);

    /**
     * Buscar estudiantes por nombre (buscar en nombre y apellido) - versión original
     */
    @Query("SELECT e FROM Estudiante e WHERE " +
           "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(e.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Estudiante> buscarPorNombre(@Param("termino") String termino);
}
