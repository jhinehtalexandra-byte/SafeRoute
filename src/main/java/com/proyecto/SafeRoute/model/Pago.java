package com.proyecto.SafeRoute.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Pago
 * Representa los pagos realizados por el servicio de transporte
 */
@Entity
@Table(name = "pagos")
@Data
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String codigo; // Ejemplo: PAG-2024-001

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(nullable = false, length = 20)
    private String estado; // PENDIENTE, PAGADO, VENCIDO, CANCELADO

    @Column(length = 50)
    private String metodoPago; // EFECTIVO, TRANSFERENCIA, TARJETA

    @Column(length = 20)
    private String mes; // Mes al que corresponde el pago

    @Column
    private Integer anio; // Año del pago

    @Column(length = 500)
    private String concepto; // Descripción del pago

    @Column(length = 100)
    private String comprobante; // Número de comprobante o referencia

    // Relación con Estudiante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    // Relación con Padre (quien realiza el pago)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id", nullable = false)
    private Usuario padre;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        
        // Generar código automático si no existe
        if (codigo == null || codigo.isEmpty()) {
            codigo = "PAG-" + LocalDate.now().getYear() + "-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Verifica si el pago está vencido
     */
    public boolean estaVencido() {
        if (fechaVencimiento == null) return false;
        if ("PAGADO".equals(estado)) return false;
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    /**
     * Verifica si el pago está pendiente
     */
    public boolean estaPendiente() {
        return "PENDIENTE".equals(estado);
    }

    /**
     * Marca el pago como pagado
     */
    public void marcarComoPagado() {
        this.estado = "PAGADO";
        this.fechaPago = LocalDate.now();
    }
}