package com.inventario_service.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String categoria; // Guardado como String (MEDICAMENTOS, CUIDADO_PERSONAL, etc.)

    @Column(name = "forma_farmaceutica")
    private String formaFarmaceutica;

    private String presentacion;

    @Column(name = "unidad_inventario")
    private String unidadInventario;

    @Column(name = "unidad_stock")
    private String unidadStock;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "fiscalizado_digemid", nullable = false)
    private boolean fiscalizadoDigemid; // <-- NUEVO EN BASE DE DATOS

    @Column(name = "registro_sanitario")
    private String registroSanitario;   // <-- NUEVO EN BASE DE DATOS
}
