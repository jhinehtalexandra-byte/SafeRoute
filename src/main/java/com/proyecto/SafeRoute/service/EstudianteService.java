package com.proyecto.SafeRoute.service;

import com.proyecto.SafeRoute.model.Estudiante;
import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.model.Ruta;
import com.proyecto.SafeRoute.repository.EstudianteRepository;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import com.proyecto.SafeRoute.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Servicio para la gestión de estudiantes
 * Implementa la lógica de negocio relacionada con estudiantes
 */
@Service
@Transactional
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RutaRepository rutaRepository;

    /**
     * Obtiene todos los estudiantes
     */
    public List<Estudiante> obtenerTodos() {
        return estudianteRepository.findAll();
    }

    /**
     * Obtiene un estudiante por ID
     */
    public Estudiante obtenerPorId(Long id) {
        return estudianteRepository.findById(Objects.requireNonNull(id, "ID no puede ser null"))
                .orElse(null);
    }

    /**
     * Obtiene un estudiante por documento
     */
    public Estudiante obtenerPorDocumento(String documento) {
        return estudianteRepository.findByDocumento(documento).orElse(null);
    }

    /**
     * Obtiene estudiantes por padre (todos)
     */
    public List<Estudiante> obtenerPorPadre(Long padreId) {
        return estudianteRepository.findByPadreId(Objects.requireNonNull(padreId, "padreId no puede ser null"));
    }

    /**
     * Obtiene estudiantes activos por padre
     */
    public List<Estudiante> obtenerActivosPorPadre(Long padreId) {
        return estudianteRepository.findByPadreIdAndActivo(Objects.requireNonNull(padreId, "padreId no puede ser null"), true);
    }

    /**
     * Obtiene estudiantes por ruta (todos)
     */
    public List<Estudiante> obtenerPorRuta(Long rutaId) {
        return estudianteRepository.findByRutaId(Objects.requireNonNull(rutaId, "rutaId no puede ser null"));
    }

    /**
     * Obtiene estudiantes activos por ruta
     */
    public List<Estudiante> obtenerActivosPorRuta(Long rutaId) {
        return estudianteRepository.findByRutaIdAndActivo(Objects.requireNonNull(rutaId, "rutaId no puede ser null"), true);
    }

    /**
     * Obtiene todos los estudiantes activos
     */
    public List<Estudiante> obtenerActivos() {
        return estudianteRepository.findByActivo(true);
    }

    /**
     * Busca estudiantes por nombre o apellido
     */
    public List<Estudiante> buscarPorNombre(String termino) {
        return estudianteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                Objects.requireNonNull(termino, "termino no puede ser null"), termino);
    }

    /**
     * Verifica si existe un documento
     */
    public boolean existePorDocumento(String documento) {
        return estudianteRepository.existsByDocumento(documento);
    }

    /**
     * Cuenta estudiantes activos
     */
    public long contarEstudiantesActivos() {
        return estudianteRepository.countByActivo(true);
    }

    /**
     * Cuenta estudiantes por padre
     */
    public long contarPorPadre(Long padreId) {
        return estudianteRepository.countByPadreId(Objects.requireNonNull(padreId, "padreId no puede ser null"));
    }

    /**
     * Cuenta estudiantes activos por ruta
     */
    public long contarActivosPorRuta(Long rutaId) {
        return estudianteRepository.countByRutaIdAndActivo(Objects.requireNonNull(rutaId, "rutaId no puede ser null"), true);
    }

    /**
     * Crea un nuevo estudiante
     */
    public Estudiante crear(Estudiante estudiante) {
        // Validar que el documento no exista
        if (estudianteRepository.existsByDocumento(estudiante.getDocumento())) {
            throw new RuntimeException("El documento ya está registrado");
        }

        // Validar que el padre exista y sea rol PADRE
        if (estudiante.getPadre() != null) {
            Long padreId = Objects.requireNonNull(estudiante.getPadre().getId(), "ID del padre no puede ser null");
            Usuario padre = usuarioRepository.findById(padreId)
                    .orElseThrow(() -> new RuntimeException("Padre no encontrado"));
            
            if (!"PADRE".equals(padre.getRol())) {
                throw new RuntimeException("El usuario asignado no tiene rol de padre");
            }
        }

        // Validar que la ruta exista y esté activa
        if (estudiante.getRuta() != null) {
            Long rutaId = Objects.requireNonNull(estudiante.getRuta().getId(), "ID de la ruta no puede ser null");
            Ruta ruta = rutaRepository.findById(rutaId)
                    .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
            
            if (!ruta.getActivo()) {
                throw new RuntimeException("La ruta seleccionada no está activa");
            }

            // Verificar capacidad de la ruta
            long estudiantesEnRuta = contarActivosPorRuta(rutaId);
            if (estudiantesEnRuta >= ruta.getCapacidadMaxima()) {
                throw new RuntimeException("La ruta ha alcanzado su capacidad máxima");
            }
        }

        // Establecer valores por defecto
        if (estudiante.getActivo() == null) {
            estudiante.setActivo(true);
        }

        return estudianteRepository.save(estudiante);
    }

    /**
     * Actualiza un estudiante existente
     */
    public Estudiante actualizar(Long id, Estudiante estudianteActualizado) {
        Objects.requireNonNull(id, "ID no puede ser null");
        
        Estudiante estudianteExistente = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Validar documento si cambió
        if (!estudianteExistente.getDocumento().equals(estudianteActualizado.getDocumento())) {
            if (estudianteRepository.existsByDocumento(estudianteActualizado.getDocumento())) {
                throw new RuntimeException("El documento ya está registrado");
            }
            estudianteExistente.setDocumento(estudianteActualizado.getDocumento());
        }

        // Validar padre si cambió
        if (estudianteActualizado.getPadre() != null) {
            Long padreId = Objects.requireNonNull(estudianteActualizado.getPadre().getId(), "ID del padre no puede ser null");
            Usuario padre = usuarioRepository.findById(padreId)
                    .orElseThrow(() -> new RuntimeException("Padre no encontrado"));
            
            if (!"PADRE".equals(padre.getRol())) {
                throw new RuntimeException("El usuario asignado no tiene rol de padre");
            }
            estudianteExistente.setPadre(padre);
        }

        // Validar ruta si cambió
        if (estudianteActualizado.getRuta() != null) {
            Long rutaId = Objects.requireNonNull(estudianteActualizado.getRuta().getId(), "ID de la ruta no puede ser null");
            Ruta ruta = rutaRepository.findById(rutaId)
                    .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
            
            if (!ruta.getActivo()) {
                throw new RuntimeException("La ruta seleccionada no está activa");
            }

            // Solo verificar capacidad si cambió de ruta
            if (estudianteExistente.getRuta() == null || 
                !estudianteExistente.getRuta().getId().equals(rutaId)) {
                long estudiantesEnRuta = contarActivosPorRuta(rutaId);
                if (estudiantesEnRuta >= ruta.getCapacidadMaxima()) {
                    throw new RuntimeException("La ruta ha alcanzado su capacidad máxima");
                }
            }
            
            estudianteExistente.setRuta(ruta);
        }

        // Actualizar otros campos
        estudianteExistente.setNombre(estudianteActualizado.getNombre());
        estudianteExistente.setApellido(estudianteActualizado.getApellido());
        estudianteExistente.setFechaNacimiento(estudianteActualizado.getFechaNacimiento());
        estudianteExistente.setDireccion(estudianteActualizado.getDireccion());
        estudianteExistente.setTelefono(estudianteActualizado.getTelefono());
        estudianteExistente.setGrado(estudianteActualizado.getGrado());
        estudianteExistente.setInstitucion(estudianteActualizado.getInstitucion());
        estudianteExistente.setActivo(estudianteActualizado.getActivo());

        return estudianteRepository.save(estudianteExistente);
    }

    /**
     * Activa o desactiva un estudiante
     */
    public Estudiante toggleEstado(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        estudiante.setActivo(!estudiante.getActivo());
        return estudianteRepository.save(estudiante);
    }

    /**
     * Elimina un estudiante (eliminación física)
     */
    public void eliminar(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        if (!estudianteRepository.existsById(id)) {
            throw new RuntimeException("Estudiante no encontrado");
        }
        estudianteRepository.deleteById(id);
    }

    /**
     * Desactiva un estudiante (eliminación lógica)
     */
    public Estudiante desactivar(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        estudiante.setActivo(false);
        return estudianteRepository.save(estudiante);
    }

    /**
     * Asigna un estudiante a una ruta
     */
    public Estudiante asignarRuta(Long estudianteId, Long rutaId) {
        Estudiante estudiante = estudianteRepository.findById(Objects.requireNonNull(estudianteId, "estudianteId no puede ser null"))
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Ruta ruta = rutaRepository.findById(Objects.requireNonNull(rutaId, "rutaId no puede ser null"))
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        if (!ruta.getActivo()) {
            throw new RuntimeException("La ruta no está activa");
        }

        // Verificar capacidad
        long estudiantesEnRuta = contarActivosPorRuta(rutaId);
        if (estudiantesEnRuta >= ruta.getCapacidadMaxima()) {
            throw new RuntimeException("La ruta ha alcanzado su capacidad máxima");
        }

        estudiante.setRuta(ruta);
        return estudianteRepository.save(estudiante);
    }

    /**
     * Remueve un estudiante de su ruta
     */
    public Estudiante removerRuta(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(Objects.requireNonNull(estudianteId, "estudianteId no puede ser null"))
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudiante.setRuta(null);
        return estudianteRepository.save(estudiante);
    }

    /**
     * Obtiene estadísticas de estudiantes
     */
    public EstudianteEstadisticas obtenerEstadisticas() {
        EstudianteEstadisticas stats = new EstudianteEstadisticas();
        
        stats.setTotalEstudiantes(estudianteRepository.count());
        stats.setEstudiantesActivos(contarEstudiantesActivos());
        stats.setEstudiantesInactivos(stats.getTotalEstudiantes() - stats.getEstudiantesActivos());
        
        return stats;
    }

    /**
     * Clase interna para estadísticas de estudiantes
     */
    public static class EstudianteEstadisticas {
        private long totalEstudiantes;
        private long estudiantesActivos;
        private long estudiantesInactivos;

        public long getTotalEstudiantes() { return totalEstudiantes; }
        public void setTotalEstudiantes(long totalEstudiantes) { this.totalEstudiantes = totalEstudiantes; }
        
        public long getEstudiantesActivos() { return estudiantesActivos; }
        public void setEstudiantesActivos(long estudiantesActivos) { this.estudiantesActivos = estudiantesActivos; }
        
        public long getEstudiantesInactivos() { return estudiantesInactivos; }
        public void setEstudiantesInactivos(long estudiantesInactivos) { this.estudiantesInactivos = estudiantesInactivos; }
    }
}
