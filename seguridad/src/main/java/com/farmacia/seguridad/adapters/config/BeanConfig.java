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
    public AuthUseCase authUseCase(UsuarioOutPutPort usuarioOutPutPort,
                                   TokenOutputPort tokenOutputPort,
                                   PasswordEncoderOutputPort passwordEncoderOutputPort) {
        return new AuthUseCase(usuarioOutPutPort, tokenOutputPort, passwordEncoderOutputPort);
    }
}
