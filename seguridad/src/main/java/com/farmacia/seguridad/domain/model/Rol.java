package com.farmacia.seguridad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    private Long id;
    private String nombreRol; // "PROPIETARIO", "FARMACEUTICO", "TECNICA"
}
