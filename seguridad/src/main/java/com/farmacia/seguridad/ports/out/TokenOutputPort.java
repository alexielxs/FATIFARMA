package com.farmacia.seguridad.ports.out;

import com.farmacia.seguridad.domain.model.Usuario;

public interface TokenOutputPort {
    String generarToken(Usuario usuario);
}
