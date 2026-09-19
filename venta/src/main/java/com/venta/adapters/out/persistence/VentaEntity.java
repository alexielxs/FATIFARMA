package com.venta.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_venta", nullable = false)
    private String tipoVenta; // Guardará "NORMAL" o "CON_RECETA" en MySQL

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @Column(nullable = false)
    private Double total;

    @Column(name = "usuario_responsable", nullable = false)
    private String usuarioResponsable;

    @Column(nullable = false)
    private String sucursal;

    @Column(name = "metodo_pago", nullable = false) // <-- NUEVO EN BASE DE DATOS: Efectivo, Yape
    private String metodoPago;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "venta")
    private List<DetalleVentaEntity> detalles;

    @Embedded
    private RecetaMedicaEmbeddable receta; // Nace vacío en ventas libres o regularizables
}
