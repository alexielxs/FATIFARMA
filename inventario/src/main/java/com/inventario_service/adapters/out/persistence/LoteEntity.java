package com.inventario_service.adapters.out.persistence;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private SucursalEntity sucursal;

    @Column(name = "codigo_lote", nullable = false)
    private String codigoLote;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate javaFechaVencimiento;

    @Column(name = "ubicacion_anaquel")
    private String ubicacionAnaquel;

    @Column(name = "usuario_registro", nullable = false)
    private String usuarioRegistro;
}
