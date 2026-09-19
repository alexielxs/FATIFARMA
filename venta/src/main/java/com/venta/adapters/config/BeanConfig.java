package com.venta.adapters.config;

import com.venta.domain.service.VentasUseCase;
import com.venta.ports.out.InventarioClientPort;
import com.venta.ports.out.VentasOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public VentasUseCase ventasUseCase(VentasOutputPort ventasOutputPort, InventarioClientPort inventarioClientPort) {
        return new VentasUseCase(ventasOutputPort, inventarioClientPort);
    }
}
