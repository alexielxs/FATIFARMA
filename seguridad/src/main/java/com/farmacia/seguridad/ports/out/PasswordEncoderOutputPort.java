package com.farmacia.seguridad.ports.out;

public interface PasswordEncoderOutputPort {
    String encriptar(String password);
    boolean verificar(String passwordPlana, String passwordEncriptada);
}
