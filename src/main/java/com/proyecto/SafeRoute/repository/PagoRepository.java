package com.proyecto.SafeRoute.repository;

import com.proyecto.SafeRoute.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pago
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    Optional<Pago> findByCodigo(String codigo);
    
    List<Pago> findByCodigoContainingIgnoreCase(String codigo);
    
    List<Pago> findByEstudianteId(Long estudianteId);
    
    List<Pago> findByPadreId(Long padreId);
    
    List<Pago> findByEstado(String estado);
    
    List<Pago> findByPadreIdAndEstado(Long padreId, String estado);
    
    List<Pago> findByEstadoAndFechaVencimientoBefore(String estado, LocalDate fecha);
    
    List<Pago> findByFechaPagoBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    List<Pago> findByEstadoAndFechaPagoBetween(String estado, LocalDate fechaInicio, LocalDate fechaFin);
    
    boolean existsByCodigo(String codigo);
    
    long countByEstado(String estado);
    
    long countByPadreIdAndEstado(Long padreId, String estado);
    
    long countByEstadoAndFechaVencimientoBefore(String estado, LocalDate fecha);
}