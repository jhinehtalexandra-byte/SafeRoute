package com.proyecto.SafeRoute.service;

import com.proyecto.SafeRoute.model.Ruta;
import com.proyecto.SafeRoute.repository.RutaRepository;
import com.proyecto.SafeRoute.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio para la gestión de rutas
 * Implementa la lógica de negocio relacionada con rutas
 */
@Service
@Transactional
public class RutaService {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    /**
     * Obtiene todas las rutas del sistema
     */
    public List<Ruta> obtenerTodas() {
        return rutaRepository.findAll();
    }

    /**
     * Obtiene una ruta por ID
     */
    public Optional<Ruta> obtenerPorId(Long id) {
        return rutaRepository.findById(Objects.requireNonNull(id, "ID no puede ser null"));
    }

    /**
     * Obtiene una ruta por código
     */
    public Optional<Ruta> obtenerPorCodigo(String codigo) {
        return rutaRepository.findByCodigo(codigo);
    }

    /**
     * Busca rutas por código (búsqueda parcial)
     */
    public List<Ruta> buscarPorCodigo(String codigo) {
        return rutaRepository.findByCodigoContainingIgnoreCase(codigo);
    }

    /**
     * Busca rutas por nombre (búsqueda parcial)
     */
    public List<Ruta> buscarPorNombre(String nombre) {
        return rutaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene todas las rutas por turno
     */
    public List<Ruta> obtenerPorTurno(String turno) {
        return rutaRepository.findByTurno(turno);
    }

    /**
     * Obtiene todas las rutas activas
     */
    public List<Ruta> obtenerRutasActivas() {
        return rutaRepository.findByActivo(true);
    }

    /**
     * Obtiene todas las rutas por estado (activo/inactivo)
     */
    public List<Ruta> obtenerPorEstado(Boolean activo) {
        return rutaRepository.findByActivo(activo);
    }

    /**
     * Obtiene rutas por conductor
     */
    public List<Ruta> obtenerPorConductor(Long conductorId) {
        Objects.requireNonNull(conductorId, "conductorId no puede ser null");
        return rutaRepository.findByConductorId(conductorId);
    }

    /**
     * Obtiene la ruta de un conductor específico (primera coincidencia)
     */
    public Optional<Ruta> obtenerRutaPorConductor(Long conductorId) {
        Objects.requireNonNull(conductorId, "conductorId no puede ser null");
        List<Ruta> rutas = obtenerPorConductor(conductorId);
        return rutas.isEmpty() ? Optional.empty() : Optional.of(rutas.get(0));
    }

    /**
     * Obtiene próximas rutas activas (limitado)
     */
    public List<Ruta> obtenerProximasRutas(int limite) {
        List<Ruta> rutasActivas = obtenerRutasActivas();
        return rutasActivas.size() > limite ? rutasActivas.subList(0, limite) : rutasActivas;
    }

    /**
     * Verifica si existe una ruta por código
     */
    public boolean existePorCodigo(String codigo) {
        return rutaRepository.existsByCodigo(codigo);
    }

    /**
     * Verifica si existe código (alias de existePorCodigo)
     */
    public boolean existeCodigo(String codigo) {
        return existePorCodigo(codigo);
    }

    /**
     * Crea una nueva ruta
     */
    public Ruta crear(Ruta ruta) {
        // Validar que el código no exista
        if (rutaRepository.existsByCodigo(ruta.getCodigo())) {
            throw new RuntimeException("El código de ruta ya está en uso");
        }

        // Establecer valores por defecto
        if (ruta.getActivo() == null) {
            ruta.setActivo(true);
        }

        // Validar capacidad máxima
        if (ruta.getCapacidadMaxima() == null || ruta.getCapacidadMaxima() <= 0) {
            throw new RuntimeException("La capacidad máxima debe ser mayor a 0");
        }

        // Validar turnos válidos
        if (ruta.getTurno() != null) {
            String turno = ruta.getTurno().toUpperCase();
            if (!turno.equals("MAÑANA") && !turno.equals("TARDE") && !turno.equals("NOCHE")) {
                throw new RuntimeException("Turno inválido. Use: MAÑANA, TARDE o NOCHE");
            }
            ruta.setTurno(turno);
        }

        return rutaRepository.save(ruta);
    }

    /**
     * Guarda una ruta (crear o actualizar)
     */
    public Ruta guardar(Ruta ruta) {
        if (ruta.getId() == null) {
            return crear(ruta);
        } else {
            return actualizar(ruta.getId(), ruta);
        }
    }

    /**
     * Actualiza una ruta existente
     */
    public Ruta actualizar(Long id, Ruta rutaActualizada) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Ruta> rutaOpt = rutaRepository.findById(id);

        if (!rutaOpt.isPresent()) {
            throw new RuntimeException("Ruta no encontrada");
        }

        Ruta rutaExistente = rutaOpt.get();

        // Validar código si cambió
        if (!rutaExistente.getCodigo().equals(rutaActualizada.getCodigo())) {
            if (rutaRepository.existsByCodigo(rutaActualizada.getCodigo())) {
                throw new RuntimeException("El código de ruta ya está en uso");
            }
            rutaExistente.setCodigo(rutaActualizada.getCodigo());
        }

        // Validar capacidad vs estudiantes asignados
        long estudiantesActuales = contarEstudiantesPorRuta(id);
        if (rutaActualizada.getCapacidadMaxima() < estudiantesActuales) {
            throw new RuntimeException(
                    "La capacidad máxima no puede ser menor a " + estudiantesActuales + 
                    " (estudiantes actualmente asignados)"
            );
        }

        // Actualizar campos
        rutaExistente.setNombre(rutaActualizada.getNombre());
        rutaExistente.setDescripcion(rutaActualizada.getDescripcion());
        rutaExistente.setHoraInicio(rutaActualizada.getHoraInicio());
        rutaExistente.setHoraFin(rutaActualizada.getHoraFin());
        rutaExistente.setTurno(rutaActualizada.getTurno());
        rutaExistente.setCapacidadMaxima(rutaActualizada.getCapacidadMaxima());
        rutaExistente.setActivo(rutaActualizada.getActivo());
        rutaExistente.setConductor(rutaActualizada.getConductor());

        return rutaRepository.save(rutaExistente);
    }

    /**
     * Activa o desactiva una ruta
     */
    public Ruta toggleEstado(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Ruta> rutaOpt = rutaRepository.findById(id);

        if (!rutaOpt.isPresent()) {
            throw new RuntimeException("Ruta no encontrada");
        }

        Ruta ruta = rutaOpt.get();
        ruta.setActivo(!ruta.getActivo());
        return rutaRepository.save(ruta);
    }

    /**
     * Elimina una ruta por ID (eliminación física)
     */
    public void eliminar(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        if (!rutaRepository.existsById(id)) {
            throw new RuntimeException("Ruta no encontrada");
        }

        // Verificar si tiene estudiantes asignados
        long estudiantes = contarEstudiantesPorRuta(id);
        if (estudiantes > 0) {
            throw new RuntimeException(
                    "No se puede eliminar la ruta porque tiene " + estudiantes + 
                    " estudiante(s) asignado(s)"
            );
        }

        rutaRepository.deleteById(id);
    }

    /**
     * Desactiva una ruta (eliminación lógica)
     */
    public Ruta desactivar(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Ruta> rutaOpt = rutaRepository.findById(id);

        if (!rutaOpt.isPresent()) {
            throw new RuntimeException("Ruta no encontrada");
        }

        Ruta ruta = rutaOpt.get();
        ruta.setActivo(false);
        return rutaRepository.save(ruta);
    }

    /**
     * Cuenta el total de rutas
     */
    public long contarTodas() {
        return rutaRepository.count();
    }

    /**
     * Cuenta las rutas activas
     */
    public long contarRutasActivas() {
        return rutaRepository.countByActivo(true);
    }

    /**
     * Cuenta rutas por turno
     */
    public long contarPorTurno(String turno) {
        return rutaRepository.countByTurno(turno);
    }

    /**
     * Cuenta estudiantes asignados a una ruta específica
     */
    public long contarEstudiantesPorRuta(Long rutaId) {
        Objects.requireNonNull(rutaId, "rutaId no puede ser null");
        return estudianteRepository.countByRutaIdAndActivo(rutaId, true);
    }

    /**
     * Calcula el porcentaje de ocupación de una ruta
     */
    public double calcularPorcentajeOcupacion(Long rutaId) {
        Objects.requireNonNull(rutaId, "rutaId no puede ser null");
        Optional<Ruta> rutaOpt = rutaRepository.findById(rutaId);
        
        if (!rutaOpt.isPresent()) {
            return 0.0;
        }

        Ruta ruta = rutaOpt.get();
        if (ruta.getCapacidadMaxima() <= 0) {
            return 0.0;
        }

        long estudiantesAsignados = contarEstudiantesPorRuta(rutaId);
        return (estudiantesAsignados * 100.0) / ruta.getCapacidadMaxima();
    }

    /**
     * Verifica si una ruta tiene capacidad disponible
     */
    public boolean tieneCapacidadDisponible(Long rutaId) {
        Objects.requireNonNull(rutaId, "rutaId no puede ser null");
        Optional<Ruta> rutaOpt = rutaRepository.findById(rutaId);
        
        if (!rutaOpt.isPresent()) {
            return false;
        }

        Ruta ruta = rutaOpt.get();
        long estudiantesAsignados = contarEstudiantesPorRuta(rutaId);
        
        return estudiantesAsignados < ruta.getCapacidadMaxima();
    }

    /**
     * Obtiene rutas con capacidad disponible
     */
    public List<Ruta> obtenerRutasConCapacidad() {
        List<Ruta> rutasActivas = obtenerRutasActivas();
        return rutasActivas.stream()
                .filter(ruta -> {
                    long estudiantes = contarEstudiantesPorRuta(ruta.getId());
                    return estudiantes < ruta.getCapacidadMaxima();
                })
                .toList();
    }

    /**
     * Obtiene estadísticas generales de rutas
     */
    public RutaEstadisticas obtenerEstadisticas() {
        RutaEstadisticas stats = new RutaEstadisticas();
        
        stats.setTotalRutas(contarTodas());
        stats.setRutasActivas(contarRutasActivas());
        stats.setRutasInactivas(contarTodas() - contarRutasActivas());
        stats.setRutasMañana(contarPorTurno("MAÑANA"));
        stats.setRutasTarde(contarPorTurno("TARDE"));
        stats.setRutasNoche(contarPorTurno("NOCHE"));
        
        return stats;
    }

    /**
     * Clase interna para estadísticas de rutas
     */
    public static class RutaEstadisticas {
        private long totalRutas;
        private long rutasActivas;
        private long rutasInactivas;
        private long rutasMañana;
        private long rutasTarde;
        private long rutasNoche;

        // Getters y Setters
        public long getTotalRutas() { return totalRutas; }
        public void setTotalRutas(long totalRutas) { this.totalRutas = totalRutas; }
        
        public long getRutasActivas() { return rutasActivas; }
        public void setRutasActivas(long rutasActivas) { this.rutasActivas = rutasActivas; }
        
        public long getRutasInactivas() { return rutasInactivas; }
        public void setRutasInactivas(long rutasInactivas) { this.rutasInactivas = rutasInactivas; }
        
        public long getRutasMañana() { return rutasMañana; }
        public void setRutasMañana(long rutasMañana) { this.rutasMañana = rutasMañana; }
        
        public long getRutasTarde() { return rutasTarde; }
        public void setRutasTarde(long rutasTarde) { this.rutasTarde = rutasTarde; }
        
        public long getRutasNoche() { return rutasNoche; }
        public void setRutasNoche(long rutasNoche) { this.rutasNoche = rutasNoche; }
    }
}
