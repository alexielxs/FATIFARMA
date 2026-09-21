package com.inventario_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lote {
    private Long id;
    private Producto producto;
    private String codigoLote;
    private Integer cantidad;
    private Double precioVenta;
    private LocalDate fechaVencimiento;
    private Sucursal sucursal;
    private String ubicacionAnaquel;
    private String usuarioRegistro;
}
