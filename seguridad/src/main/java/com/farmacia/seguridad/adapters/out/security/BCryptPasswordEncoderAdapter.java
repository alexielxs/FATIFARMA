package com.farmacia.seguridad.adapters.out.security;

import com.farmacia.seguridad.ports.out.PasswordEncoderOutputPort;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderOutputPort {
    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptPasswordEncoderAdapter() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encriptar(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verificar(String passwordPlana, String passwordEncriptada) {
        return passwordEncoder.matches(passwordPlana, passwordEncriptada);
    }
}
