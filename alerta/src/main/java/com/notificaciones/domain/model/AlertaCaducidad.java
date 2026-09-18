package com.notificaciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaCaducidad {
    private String producto;
    private String lote;
    private String fechaVencimiento;
    private long diasRestantes;
    private String estado;
}
