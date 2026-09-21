package com.inventario_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sucursal {
    private Long id;
    private String nombreSucursal; // "SUCURSAL_PRINCIPAL", "SUCURSAL_SECUNDARIA"
}
