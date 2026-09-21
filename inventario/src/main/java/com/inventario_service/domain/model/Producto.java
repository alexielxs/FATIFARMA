package com.inventario_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    private Long id;
    private Categoria categoria; // <-- Objeto Categoria puro del dominio
    private String nombre;
    private String formaFarmaceutica;
    private String presentacion;
    private String unidadInventario;
    private String unidadStock;
    private Integer stockMinimo;
    private boolean fiscalizadoDigemid;
    private String registroSanitario;
    private String codigoMedicamento;
}

