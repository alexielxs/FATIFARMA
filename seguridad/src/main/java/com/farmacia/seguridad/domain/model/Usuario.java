package com.farmacia.seguridad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                // Genera automáticamente todos los Getters, Setters y toString
@NoArgsConstructor   // Genera el constructor vacío (Usuario())
@AllArgsConstructor  // Genera el constructor con todos los campos (Usuario(id, email, password, rol))
public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
}
