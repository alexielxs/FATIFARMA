package com.notificaciones.adapters.config;

import com.notificaciones.domain.service.NotificacionUseCase;
import com.notificaciones.ports.out.InventarioClientPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public NotificacionUseCase notificacionUseCase(InventarioClientPort inventarioClientPort) {
        return new NotificacionUseCase(inventarioClientPort);
    }
}
