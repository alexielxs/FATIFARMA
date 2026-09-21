package com.farmacia.seguridad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
}






