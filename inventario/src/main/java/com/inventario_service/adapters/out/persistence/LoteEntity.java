package com.inventario_service.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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

    @Column(name = "codigo_lote", nullable = false)
    private String codigoLote;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private String sucursal; // Guardado como String (SUCURSAL_PRINCIPAL, SUCURSAL_SECUNDARIA)

    @Column(name = "usuario_registro", nullable = false)
    private String usuarioRegistro; // <-- NUEVO: Columna para la auditoría de registro

    // RELACIÓN: Muchos lotes pueden pertenecer al mismo producto
    @ManyToOne(cascade = CascadeType.PERSIST) // Si el producto no existe en el catálogo, lo crea automáticamente
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;
}

