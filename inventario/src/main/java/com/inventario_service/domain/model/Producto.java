package com.inventario_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private Long id;
    private String nombre;
    private Categoria categoria;
    private String formaFarmaceutica;
    private String presentacion;
    private String unidadInventario;
    private String unidadStock;
    private Integer stockMinimo;
    private boolean fiscalizadoDigemid; // <-- NUEVO: true si requiere receta retenida y control especial
    private String registroSanitario;   // <-- NUEVO: Código oficial otorgado por DIGEMID (Ej: NG-1234)
}

