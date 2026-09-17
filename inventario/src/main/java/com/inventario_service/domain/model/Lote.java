package com.inventario_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lote {
    private Long id;
    private String codigoLote;         // Ej: LOTE-2026A
    private Integer cantidad;          // Stock disponible en este lote
    private Double precioVenta;        // Precio al público
    private LocalDate fechaVencimiento;// Fecha de caducidad
    private Sucursal sucursal;         // En cuál de las dos farmacias está físicamente
    private Producto producto;         // El producto pertenece al lote
    private String usuarioRegistro;    // <-- NUEVO: Guarda el correo de quien registró el lote
}
