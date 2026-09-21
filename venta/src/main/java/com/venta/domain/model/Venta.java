package com.venta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {
    private Long id;
    private String tipoVenta; // NORMAL o CON_RECETA
    private LocalDateTime fechaVenta;
    private Double total;
    private String usuarioResponsable;
    private String sucursal;
    private String metodoPago;
    private List<DetalleVenta> detalles;
    private RecetaMedica receta; // Componente embebido opcional
}