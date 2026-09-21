package com.venta.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // <-- IMPORTANTE: Importación para mitigar bucles recursivos

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
    private String tipoVenta; // Guardará "NORMAL" o "CON_RECETA" de forma inmutable

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta; // Sella el momento exacto del cobro comercial

    @Column(nullable = false)
    private Double total;

    @Column(name = "usuario_responsable", nullable = false)
    private String usuarioResponsable; // Email de la técnica logueada en caja

    @Column(nullable = false)
    private String sucursal; // 'FATIFARMA_ATE_CENTRAL' o 'FATIFARMA_ATE_PRINCIPAL'

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago; // 'EFECTIVO', 'YAPE', 'PLIN'

    // CORREGIDO: Se cambia a FetchType.EAGER para jalar el desglose de productos al instante
    // y se añade @ToString.Exclude para anular desbordamientos de memoria (StackOverflowError).
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "venta")
    @ToString.Exclude
    private List<DetalleVentaEntity> detalles;

    @Embedded
    private RecetaMedicaEmbeddable receta; // Folder digital embebido de auditoría (DIGEMID)
}
