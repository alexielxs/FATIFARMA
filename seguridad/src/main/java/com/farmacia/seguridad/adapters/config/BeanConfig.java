package com.farmacia.seguridad.adapters.config;

import com.farmacia.seguridad.domain.service.AuthUseCase;
import com.farmacia.seguridad.ports.out.PasswordEncoderOutputPort;
import com.farmacia.seguridad.ports.out.TokenOutputPort;
import com.farmacia.seguridad.ports.out.UsuarioOutPutPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public AuthUseCase authUseCase(UsuarioOutPutPort usuarioOutputPort,
                                   TokenOutputPort tokenOutputPort,
                                   PasswordEncoderOutputPort passwordEncoderOutputPort) {
        // Aquí unimos de forma manual los adaptadores con las reglas de negocio
        return new AuthUseCase(usuarioOutputPort, tokenOutputPort, passwordEncoderOutputPort);
    }
}
