package com.proyecto.SafeRoute.controller;

import com.proyecto.SafeRoute.model.Ruta;
import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.service.RutaService;
import com.proyecto.SafeRoute.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de rutas (CRUD completo)
 * Solo accesible para usuarios con rol ADMIN
 */
@Controller
@RequestMapping("/rutas")
@PreAuthorize("hasRole('ADMIN')")
public class RutasController {

    @Autowired
    private RutaService rutaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todas las rutas del sistema
     */
    @GetMapping
    public String listarRutas(Model model, Authentication authentication) {
        try {
            // Usuario autenticado
            if (authentication != null) {
                String username = authentication.getName();
                usuarioService.obtenerPorUsername(username).ifPresent(usuario -> {
                    model.addAttribute("usuario", usuario);
                });
            }

            List<Ruta> rutas = rutaService.obtenerTodas();
            model.addAttribute("rutas", rutas);
            
            // Estadísticas
            model.addAttribute("totalRutas", rutas.size());
            model.addAttribute("rutasActivas", rutaService.contarRutasActivas());
            
            return "GestionRutas";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las rutas: " + e.getMessage());
            return "Error";
        }
    }

    /**
     * Muestra el formulario para crear una nueva ruta
     */
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model, Authentication authentication) {
        try {
            // Usuario autenticado
            if (authentication != null) {
                String username = authentication.getName();
                usuarioService.obtenerPorUsername(username).ifPresent(usuario -> {
                    model.addAttribute("usuario", usuario);
                });
            }

            model.addAttribute("ruta", new Ruta());
            
            // Lista de conductores disponibles
            List<Usuario> conductores = usuarioService.obtenerConductoresActivos();
            model.addAttribute("conductores", conductores);
            
            return "FormCrearRuta";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "Error";
        }
    }

    /**
     * Muestra el formulario para editar una ruta existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Long id, 
            Model model, 
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            // Usuario autenticado
            if (authentication != null) {
                String username = authentication.getName();
                usuarioService.obtenerPorUsername(username).ifPresent(usuario -> {
                    model.addAttribute("usuario", usuario);
                });
            }

            if (id == null) {
                redirectAttributes.addFlashAttribute("error", "ID de ruta inválido");
                return "redirect:/rutas";
            }

            Optional<Ruta> rutaOpt = rutaService.obtenerPorId(id);

            if (!rutaOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ruta no encontrada");
                return "redirect:/rutas";
            }

            model.addAttribute("ruta", rutaOpt.get());
            
            // Lista de conductores disponibles
            List<Usuario> conductores = usuarioService.obtenerConductoresActivos();
            model.addAttribute("conductores", conductores);
            
            return "FormCrearRuta";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar la ruta: " + e.getMessage());
            return "redirect:/rutas";
        }
    }

    /**
     * Guarda o actualiza una ruta
     */
    @PostMapping("/guardar")
    public String guardarRuta(
            @RequestParam(required = false) Long id,
            @RequestParam String codigo,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String horaInicio,
            @RequestParam(required = false) String horaFin,
            @RequestParam String turno,
            @RequestParam Integer capacidadMaxima,
            @RequestParam(required = false) Long conductorId,
            @RequestParam Boolean activo,
            RedirectAttributes redirectAttributes) {

        try {
            Ruta ruta;

            if (id != null) {
                // Actualizar ruta existente
                Optional<Ruta> rutaOpt = rutaService.obtenerPorId(id);

                if (!rutaOpt.isPresent()) {
                    throw new RuntimeException("Ruta no encontrada");
                }

                ruta = rutaOpt.get();

                // Validar que el código no esté en uso por otra ruta
                Optional<Ruta> rutaPorCodigo = rutaService.obtenerPorCodigo(codigo);
                if (rutaPorCodigo.isPresent() && !rutaPorCodigo.get().getId().equals(id)) {
                    throw new RuntimeException("El código de ruta ya está en uso");
                }

            } else {
                // Crear nueva ruta
                ruta = new Ruta();

                // Validar que el código no exista
                if (rutaService.existePorCodigo(codigo)) {
                    throw new RuntimeException("El código de ruta ya está en uso");
                }
            }

            // Actualizar datos básicos
            ruta.setCodigo(codigo);
            ruta.setNombre(nombre);
            ruta.setDescripcion(descripcion);
            ruta.setTurno(turno);
            ruta.setCapacidadMaxima(capacidadMaxima);
            ruta.setActivo(activo);

            // Convertir horas de String a LocalTime
            if (horaInicio != null && !horaInicio.trim().isEmpty()) {
                ruta.setHoraInicio(LocalTime.parse(horaInicio));
            }
            if (horaFin != null && !horaFin.trim().isEmpty()) {
                ruta.setHoraFin(LocalTime.parse(horaFin));
            }

            // Asignar conductor si se proporcionó
            if (conductorId != null) {
                Optional<Usuario> conductorOpt = usuarioService.obtenerPorId(conductorId);
                if (conductorOpt.isPresent()) {
                    Usuario conductor = conductorOpt.get();
                    if (!"CONDUCTOR".equals(conductor.getRol())) {
                        throw new RuntimeException("El usuario seleccionado no es un conductor");
                    }
                    ruta.setConductor(conductor);
                } else {
                    throw new RuntimeException("Conductor no encontrado");
                }
            } else {
                ruta.setConductor(null);
            }

            // Guardar ruta
            if (id != null) {
                rutaService.actualizar(id, ruta);
            } else {
                rutaService.crear(ruta);
            }

            redirectAttributes.addFlashAttribute("mensaje",
                    id != null ? "Ruta actualizada correctamente" : "Ruta creada correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar ruta: " + e.getMessage());
            return id != null ? "redirect:/rutas/editar/" + id : "redirect:/rutas/nueva";
        }

        return "redirect:/rutas";
    }

    /**
     * Elimina una ruta
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarRuta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (id == null) {
                throw new RuntimeException("ID de ruta inválido");
            }

            Optional<Ruta> rutaOpt = rutaService.obtenerPorId(id);

            if (!rutaOpt.isPresent()) {
                throw new RuntimeException("Ruta no encontrada");
            }

            // Verificar si la ruta tiene estudiantes asignados
            long estudiantesAsignados = rutaService.contarEstudiantesPorRuta(id);
            if (estudiantesAsignados > 0) {
                throw new RuntimeException(
                    "No se puede eliminar la ruta porque tiene " + estudiantesAsignados + 
                    " estudiante(s) asignado(s). Primero reasigne los estudiantes a otra ruta."
                );
            }

            rutaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Ruta eliminada correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar ruta: " + e.getMessage());
        }

        return "redirect:/rutas";
    }

    /**
     * Activa o desactiva una ruta
     */
    @PostMapping("/toggle-estado/{id}")
    public String toggleEstadoRuta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (id == null) {
                throw new RuntimeException("ID de ruta inválido");
            }

            Optional<Ruta> rutaOpt = rutaService.obtenerPorId(id);

            if (!rutaOpt.isPresent()) {
                throw new RuntimeException("Ruta no encontrada");
            }

            Ruta ruta = rutaService.toggleEstado(id);
            String estado = ruta.getActivo() ? "activada" : "desactivada";
            
            redirectAttributes.addFlashAttribute("mensaje", "Ruta " + estado + " correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
        }

        return "redirect:/rutas";
    }

    /**
     * Ver detalles de una ruta específica
     */
    @GetMapping("/detalle/{id}")
    public String verDetalleRuta(
            @PathVariable Long id, 
            Model model, 
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            // Usuario autenticado
            if (authentication != null) {
                String username = authentication.getName();
                usuarioService.obtenerPorUsername(username).ifPresent(usuario -> {
                    model.addAttribute("usuario", usuario);
                });
            }

            if (id == null) {
                redirectAttributes.addFlashAttribute("error", "ID de ruta inválido");
                return "redirect:/rutas";
            }

            Optional<Ruta> rutaOpt = rutaService.obtenerPorId(id);

            if (!rutaOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ruta no encontrada");
                return "redirect:/rutas";
            }

            Ruta ruta = rutaOpt.get();
            model.addAttribute("ruta", ruta);
            
            // Estadísticas de la ruta
            long totalEstudiantes = rutaService.contarEstudiantesPorRuta(id);
            model.addAttribute("totalEstudiantes", totalEstudiantes);
            
            double porcentajeOcupacion = ruta.getCapacidadMaxima() > 0 
                ? (totalEstudiantes * 100.0) / ruta.getCapacidadMaxima() 
                : 0;
            model.addAttribute("porcentajeOcupacion", String.format("%.2f", porcentajeOcupacion));
            
            return "DetalleRuta";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar los detalles: " + e.getMessage());
            return "redirect:/rutas";
        }
    }

    /**
     * Buscar rutas por criterios
     */
    @GetMapping("/buscar")
    public String buscarRutas(
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String turno,
            @RequestParam(required = false) Boolean activo,
            Model model,
            Authentication authentication) {
        try {
            // Usuario autenticado
            if (authentication != null) {
                String username = authentication.getName();
                usuarioService.obtenerPorUsername(username).ifPresent(usuario -> {
                    model.addAttribute("usuario", usuario);
                });
            }

            List<Ruta> rutas;

            // Aplicar filtros según los parámetros proporcionados
            if (codigo != null && !codigo.trim().isEmpty()) {
                rutas = rutaService.buscarPorCodigo(codigo);
            } else if (nombre != null && !nombre.trim().isEmpty()) {
                rutas = rutaService.buscarPorNombre(nombre);
            } else if (turno != null && !turno.trim().isEmpty()) {
                rutas = rutaService.obtenerPorTurno(turno);
            } else if (activo != null) {
                rutas = rutaService.obtenerPorEstado(activo);
            } else {
                rutas = rutaService.obtenerTodas();
            }

            model.addAttribute("rutas", rutas);
            model.addAttribute("totalRutas", rutas.size());
            
            // Mantener valores de búsqueda
            model.addAttribute("busquedaCodigo", codigo);
            model.addAttribute("busquedaNombre", nombre);
            model.addAttribute("busquedaTurno", turno);
            model.addAttribute("busquedaActivo", activo);

            return "GestionRutas";

        } catch (Exception e) {
            model.addAttribute("error", "Error en la búsqueda: " + e.getMessage());
            return "Error";
        }
    }
}