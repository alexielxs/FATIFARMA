package com.venta.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lote_id", nullable = false)
    private Long loteId; // Conector lógico por red hacia inventario-service

    @Column(name = "producto_nombre", nullable = false)
    private String productoNombre; // Jalará el nombre científico desde el almacén

    @Column(nullable = false)
    private Integer cantidad; // Cantidad física de pastillas/unidades vendidas

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario; // Precio inmutable sellado en caja

    // CORREGIDO: Se cambia a EAGER para garantizar que Hibernate jale los detalles
    // al consultar el historial o reportes en milisegundos sin congelar la sesión.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venta_id", nullable = false)
    private VentaEntity venta;
}
