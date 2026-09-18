package com.notificaciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaStock {
    private String producto;
    private Integer stockActual;
    private Integer stockMinimo;
    private String estado;
}
