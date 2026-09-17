package com.inventario_service.adapters.config;

import com.inventario_service.domain.service.InventarioUseCase;
import com.inventario_service.ports.out.InventarioOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public InventarioUseCase inventarioUseCase(InventarioOutputPort inventarioOutputPort) {
        return new InventarioUseCase(inventarioOutputPort);
    }
}
