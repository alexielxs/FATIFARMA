package com.farmacia.seguridad.ports.out;

import com.farmacia.seguridad.domain.model.Rol;
import com.farmacia.seguridad.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioOutputPort {

    Usuario guardarUsuario(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Rol> buscarRolPorNombre(String nombreRol);

    // Permite rastrear si un empleado intenta suplantar un rol usando un correo alternativo
    // =========================================================================
    Optional<Usuario> buscarPorNombre(String nombre);
}
