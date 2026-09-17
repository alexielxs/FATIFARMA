package com.farmacia.seguridad.ports.out;

import com.farmacia.seguridad.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioOutPutPort {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
}
