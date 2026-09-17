package com.farmacia.seguridad.ports.in;

import com.farmacia.seguridad.domain.model.Usuario;

public interface AuthInpurtPort {
    Usuario registrar(Usuario usuario);
    String login(String email, String password);
}
