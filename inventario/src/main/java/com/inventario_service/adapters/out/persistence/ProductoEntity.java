package com.inventario_service.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "forma_farmaceutica")
    private String formaFarmaceutica;

    private String presentacion;

    @Column(name = "unidad_inventario", nullable = false)
    private String unidadInventario;

    @Column(name = "unidad_stock", nullable = false)
    private String unidadStock;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "fiscalizado_digemid", nullable = false)
    private boolean fiscalizadoDigemid;

    @Column(name = "registro_sanitario")
    private String registroSanitario;

    @Column(name = "codigo_medicamento", unique = true)
    private String codigoMedicamento;
}
