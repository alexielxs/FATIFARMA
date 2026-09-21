package com.venta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder; // <-- NUEVO IMPORT AUTOMÁTICO DE LOMBOK
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // <-- ACTIVADO: Habilita el uso de .builder() en tu VentasController
public class DetalleVenta {
    private Long id;
    private Long loteId;
    private String productoNombre;
    private Integer cantidad;
    private Double precioUnitario;
}
