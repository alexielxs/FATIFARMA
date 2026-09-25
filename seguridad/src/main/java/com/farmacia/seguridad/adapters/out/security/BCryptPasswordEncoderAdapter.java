package com.farmacia.seguridad.adapters.out.security;

import com.farmacia.seguridad.ports.out.PasswordEncoderOutputPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderOutputPort {

    private final BCryptPasswordEncoder passwordEncoder;

    // OPTIMIZACIÓN: Inyectamos el BCryptPasswordEncoder en lugar de instanciarlo con 'new'.
    // Esto aprovecha el Bean que ya declaraste e inicializaste en tu clase 'SecurityConfig'.
    public BCryptPasswordEncoderAdapter(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
