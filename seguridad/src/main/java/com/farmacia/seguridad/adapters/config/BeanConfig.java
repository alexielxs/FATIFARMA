package com.farmacia.seguridad.adapters.config;

import com.farmacia.seguridad.domain.service.AuthUseCase;
import com.farmacia.seguridad.ports.in.SeguridadInputPort;
import com.farmacia.seguridad.ports.out.PasswordEncoderOutputPort;
import com.farmacia.seguridad.ports.out.TokenOutputPort;
import com.farmacia.seguridad.ports.out.UsuarioOutputPort;
import com.farmacia.seguridad.ports.out.NotificacionesClientPort; // <-- VERIFICA ESTE IMPORT OBLIGATORIO
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public SeguridadInputPort authUseCase(UsuarioOutputPort usuarioOutputPort,
                                          TokenOutputPort tokenOutputPort,
                                          PasswordEncoderOutputPort passwordEncoderOutputPort,
                                          NotificacionesClientPort notificacionesClientPort) { // <-- AGREGADO AQUÍ

        // CORREGIDO: Se inyecta el cuarto argumento para cumplir con la firma del nuevo constructor
        return new AuthUseCase(usuarioOutputPort, tokenOutputPort, passwordEncoderOutputPort, notificacionesClientPort);
    }
}
