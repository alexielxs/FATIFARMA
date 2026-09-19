package com.venta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {
    private Long id;
    private Long loteId;
    private String productoNombre;
    private  Integer cantidad;
    private Double precioUnitario;
}
