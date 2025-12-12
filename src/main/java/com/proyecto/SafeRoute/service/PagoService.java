package com.proyecto.SafeRoute.service;

import com.proyecto.SafeRoute.model.Pago;
import com.proyecto.SafeRoute.model.Usuario;
import com.proyecto.SafeRoute.repository.PagoRepository;
import com.proyecto.SafeRoute.repository.EstudianteRepository;
import com.proyecto.SafeRoute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de pagos
 * Implementa la lógica de negocio relacionada con pagos
 */
@Service
@Transactional
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene todos los pagos
     */
    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }

    /**
     * Obtiene un pago por ID
     */
    public Optional<Pago> obtenerPorId(Long id) {
        return pagoRepository.findById(Objects.requireNonNull(id, "ID no puede ser null"));
    }

    /**
     * Obtiene un pago por código
     */
    public Optional<Pago> obtenerPorCodigo(String codigo) {
        return pagoRepository.findByCodigo(codigo);
    }

    /**
     * Obtiene pagos por estudiante
     */
    public List<Pago> obtenerPorEstudiante(Long estudianteId) {
        Objects.requireNonNull(estudianteId, "estudianteId no puede ser null");
        return pagoRepository.findByEstudianteId(estudianteId);
    }

    /**
     * Obtiene pagos por padre (todos)
     */
    public List<Pago> obtenerPorPadre(Long padreId) {
        Objects.requireNonNull(padreId, "padreId no puede ser null");
        return pagoRepository.findByPadreId(padreId);
    }

    /**
     * Obtiene pagos recientes por padre con límite
     * MÉTODO REQUERIDO POR DASHBOARDCONTROLLER
     */
    public List<Pago> obtenerPagosPorPadre(Long padreId, int limite) {
        Objects.requireNonNull(padreId, "padreId no puede ser null");
        List<Pago> pagos = pagoRepository.findByPadreId(padreId);
        // Ordenar por fecha de pago descendente y limitar
        return pagos.stream()
                .sorted((p1, p2) -> {
                    LocalDate fecha1 = p1.getFechaPago() != null ? p1.getFechaPago() : LocalDate.MIN;
                    LocalDate fecha2 = p2.getFechaPago() != null ? p2.getFechaPago() : LocalDate.MIN;
                    return fecha2.compareTo(fecha1);
                })
                .limit(limite)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene próximos pagos pendientes por padre
     * MÉTODO REQUERIDO POR DASHBOARDCONTROLLER
     */
    public List<Pago> obtenerProximosPagos(Long padreId) {
        Objects.requireNonNull(padreId, "padreId no puede ser null");
        return pagoRepository.findByPadreIdAndEstado(padreId, "PENDIENTE");
    }

    /**
     * Obtiene pagos por estado
     */
    public List<Pago> obtenerPorEstado(String estado) {
        return pagoRepository.findByEstado(estado);
    }

    /**
     * Obtiene pagos pendientes
     */
    public List<Pago> obtenerPendientes() {
        return pagoRepository.findByEstado("PENDIENTE");
    }

    /**
     * Obtiene pagos pendientes por padre
     */
    public List<Pago> obtenerPendientesPorPadre(Long padreId) {
        Objects.requireNonNull(padreId, "padreId no puede ser null");
        return pagoRepository.findByPadreIdAndEstado(padreId, "PENDIENTE");
    }

    /**
     * Obtiene pagos vencidos (fecha vencimiento pasada y estado PENDIENTE)
     */
    public List<Pago> obtenerVencidos() {
        LocalDate hoy = LocalDate.now();
        return pagoRepository.findByEstadoAndFechaVencimientoBefore("PENDIENTE", hoy);
    }

    /**
     * Obtiene pagos por rango de fechas
     */
    public List<Pago> obtenerPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return pagoRepository.findByFechaPagoBetween(fechaInicio, fechaFin);
    }

    /**
     * Obtiene pagos del mes actual
     */
    public List<Pago> obtenerDelMesActual() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        return obtenerPorRangoFechas(inicioMes, finMes);
    }

    /**
     * Busca pagos por código (búsqueda parcial)
     */
    public List<Pago> buscarPorCodigo(String codigo) {
        return pagoRepository.findByCodigoContainingIgnoreCase(codigo);
    }

    /**
     * Cuenta pagos pendientes totales
     */
    public long contarPendientes() {
        return pagoRepository.countByEstado("PENDIENTE");
    }

    /**
     * Cuenta pagos pendientes por padre
     * MÉTODO REQUERIDO POR DASHBOARDCONTROLLER
     */
    public long contarPagosPendientes(Long padreId) {
        Objects.requireNonNull(padreId, "padreId no puede ser null");
        return pagoRepository.countByPadreIdAndEstado(padreId, "PENDIENTE");
    }

    /**
     * Cuenta pagos pendientes por padre (alias del anterior)
     */
    public long contarPendientesPorPadre(Long padreId) {
        return contarPagosPendientes(padreId);
    }

    /**
     * Cuenta pagos vencidos
     */
    public long contarVencidos() {
        LocalDate hoy = LocalDate.now();
        return pagoRepository.countByEstadoAndFechaVencimientoBefore("PENDIENTE", hoy);
    }

    /**
     * Calcula el total de ingresos
     */
    public BigDecimal calcularTotalIngresos() {
        List<Pago> pagosPagados = pagoRepository.findByEstado("PAGADO");
        return pagosPagados.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula ingresos del mes actual
     */
    public BigDecimal calcularIngresosMesActual() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        
        List<Pago> pagosMes = pagoRepository.findByEstadoAndFechaPagoBetween(
                "PAGADO", inicioMes, finMes
        );
        
        return pagosMes.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el total pendiente por cobrar
     */
    public BigDecimal calcularTotalPendiente() {
        List<Pago> pagosPendientes = obtenerPendientes();
        return pagosPendientes.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Genera un código único para el pago
     */
    private String generarCodigoPago() {
        LocalDate hoy = LocalDate.now();
        String fecha = hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Contar pagos del día para generar secuencia
        long pagosHoy = pagoRepository.count();
        String secuencia = String.format("%04d", pagosHoy + 1);
        
        return "PAG-" + fecha + "-" + secuencia;
    }

    /**
     * Crea un nuevo pago
     */
    public Pago crear(Pago pago) {
        // Generar código si no viene
        if (pago.getCodigo() == null || pago.getCodigo().isEmpty()) {
            pago.setCodigo(generarCodigoPago());
        } else {
            // Validar que el código no exista
            if (pagoRepository.existsByCodigo(pago.getCodigo())) {
                throw new RuntimeException("El código de pago ya existe");
            }
        }

        // Validar que el estudiante exista
        if (pago.getEstudiante() != null) {
            Long estudianteId = Objects.requireNonNull(pago.getEstudiante().getId(), "ID del estudiante no puede ser null");
            estudianteRepository.findById(estudianteId)
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        } else {
            throw new RuntimeException("El estudiante es requerido");
        }

        // Validar que el padre exista y sea rol PADRE
        if (pago.getPadre() != null) {
            Long padreId = Objects.requireNonNull(pago.getPadre().getId(), "ID del padre no puede ser null");
            Usuario padre = usuarioRepository.findById(padreId)
                    .orElseThrow(() -> new RuntimeException("Padre no encontrado"));
            
            if (!"PADRE".equals(padre.getRol())) {
                throw new RuntimeException("El usuario no tiene rol de padre");
            }
        } else {
            throw new RuntimeException("El padre es requerido");
        }

        // Establecer estado por defecto
        if (pago.getEstado() == null || pago.getEstado().isEmpty()) {
            pago.setEstado("PENDIENTE");
        }

        // Validar monto
        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }

        return pagoRepository.save(pago);
    }

    /**
     * Actualiza un pago existente
     */
    public Pago actualizar(Long id, Pago pagoActualizado) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Pago> pagoOpt = pagoRepository.findById(id);

        if (!pagoOpt.isPresent()) {
            throw new RuntimeException("Pago no encontrado");
        }

        Pago pagoExistente = pagoOpt.get();

        // Validar código si cambió
        if (!pagoExistente.getCodigo().equals(pagoActualizado.getCodigo())) {
            if (pagoRepository.existsByCodigo(pagoActualizado.getCodigo())) {
                throw new RuntimeException("El código de pago ya existe");
            }
            pagoExistente.setCodigo(pagoActualizado.getCodigo());
        }

        // Actualizar campos
        pagoExistente.setMonto(pagoActualizado.getMonto());
        pagoExistente.setFechaPago(pagoActualizado.getFechaPago());
        pagoExistente.setFechaVencimiento(pagoActualizado.getFechaVencimiento());
        pagoExistente.setEstado(pagoActualizado.getEstado());
        pagoExistente.setMetodoPago(pagoActualizado.getMetodoPago());
        pagoExistente.setMes(pagoActualizado.getMes());
        pagoExistente.setAnio(pagoActualizado.getAnio());
        pagoExistente.setConcepto(pagoActualizado.getConcepto());
        pagoExistente.setComprobante(pagoActualizado.getComprobante());
        pagoExistente.setEstudiante(pagoActualizado.getEstudiante());
        pagoExistente.setPadre(pagoActualizado.getPadre());

        return pagoRepository.save(pagoExistente);
    }

    /**
     * Guarda un pago (crear o actualizar)
     */
    public Pago guardar(Pago pago) {
        if (pago.getId() == null) {
            return crear(pago);
        } else {
            return actualizar(pago.getId(), pago);
        }
    }

    /**
     * Marca un pago como pagado
     */
    public Pago marcarComoPagado(Long id, String metodoPago) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Pago> pagoOpt = pagoRepository.findById(id);

        if (!pagoOpt.isPresent()) {
            throw new RuntimeException("Pago no encontrado");
        }

        Pago pago = pagoOpt.get();
        
        if ("PAGADO".equals(pago.getEstado())) {
            throw new RuntimeException("El pago ya está marcado como pagado");
        }

        pago.setEstado("PAGADO");
        pago.setFechaPago(LocalDate.now());
        pago.setMetodoPago(metodoPago);

        return pagoRepository.save(pago);
    }

    /**
     * Marca un pago como vencido
     */
    public Pago marcarComoVencido(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Optional<Pago> pagoOpt = pagoRepository.findById(id);

        if (!pagoOpt.isPresent()) {
            throw new RuntimeException("Pago no encontrado");
        }

        Pago pago = pagoOpt.get();
        pago.setEstado("VENCIDO");

        return pagoRepository.save(pago);
    }

    /**
     * Actualiza estados de pagos vencidos
     */
    public int actualizarPagosVencidos() {
        LocalDate hoy = LocalDate.now();
        List<Pago> pagosPendientes = pagoRepository.findByEstadoAndFechaVencimientoBefore(
                "PENDIENTE", hoy
        );

        int actualizados = 0;
        for (Pago pago : pagosPendientes) {
            pago.setEstado("VENCIDO");
            pagoRepository.save(pago);
            actualizados++;
        }

        return actualizados;
    }

    /**
     * Elimina un pago
     */
    public void eliminar(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        if (!pagoRepository.existsById(id)) {
            throw new RuntimeException("Pago no encontrado");
        }
        pagoRepository.deleteById(id);
    }

    /**
     * Obtiene estadísticas de pagos
     */
    public PagoEstadisticas obtenerEstadisticas() {
        PagoEstadisticas stats = new PagoEstadisticas();
        
        stats.setTotalPagos(pagoRepository.count());
        stats.setPagosPendientes(contarPendientes());
        stats.setPagosVencidos(contarVencidos());
        stats.setPagosPagados(pagoRepository.countByEstado("PAGADO"));
        stats.setIngresosTotales(calcularTotalIngresos());
        stats.setIngresosMesActual(calcularIngresosMesActual());
        stats.setTotalPendienteCobro(calcularTotalPendiente());
        
        return stats;
    }

    /**
     * Clase interna para estadísticas de pagos
     */
    public static class PagoEstadisticas {
        private long totalPagos;
        private long pagosPendientes;
        private long pagosVencidos;
        private long pagosPagados;
        private BigDecimal ingresosTotales;
        private BigDecimal ingresosMesActual;
        private BigDecimal totalPendienteCobro;

        public long getTotalPagos() { return totalPagos; }
        public void setTotalPagos(long totalPagos) { this.totalPagos = totalPagos; }
        
        public long getPagosPendientes() { return pagosPendientes; }
        public void setPagosPendientes(long pagosPendientes) { this.pagosPendientes = pagosPendientes; }
        
        public long getPagosVencidos() { return pagosVencidos; }
        public void setPagosVencidos(long pagosVencidos) { this.pagosVencidos = pagosVencidos; }
        
        public long getPagosPagados() { return pagosPagados; }
        public void setPagosPagados(long pagosPagados) { this.pagosPagados = pagosPagados; }
        
        public BigDecimal getIngresosTotales() { return ingresosTotales; }
        public void setIngresosTotales(BigDecimal ingresosTotales) { this.ingresosTotales = ingresosTotales; }
        
        public BigDecimal getIngresosMesActual() { return ingresosMesActual; }
        public void setIngresosMesActual(BigDecimal ingresosMesActual) { this.ingresosMesActual = ingresosMesActual; }
        
        public BigDecimal getTotalPendienteCobro() { return totalPendienteCobro; }
        public void setTotalPendienteCobro(BigDecimal totalPendienteCobro) { this.totalPendienteCobro = totalPendienteCobro; }
    }
}
