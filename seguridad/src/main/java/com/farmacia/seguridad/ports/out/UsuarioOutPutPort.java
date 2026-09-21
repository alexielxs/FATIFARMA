package com.farmacia.seguridad.ports.out;

import com.farmacia.seguridad.domain.model.Rol;
import com.farmacia.seguridad.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioOutPutPort {
    Usuario guardarUsuario(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Rol> buscarRolPorNombre(String nombreRol);
}
