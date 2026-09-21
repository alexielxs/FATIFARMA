package com.farmacia.seguridad.ports.in;

import com.farmacia.seguridad.domain.model.Usuario;

import java.util.Map;

public interface SeguridadInputPort {
    Usuario registrarUsuario(Usuario usuario, String nombreRol);
    String autenticarUsuario(String email, String password);
    Map<String, Object> solicitarRecuperacionClave(String email);
}
