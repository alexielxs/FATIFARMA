package com.venta.adapters.config;

import com.venta.domain.service.VentasUseCase;
import com.venta.ports.in.VentasInputPort; // <-- IMPORTANTE: Retornar el puerto de entrada
import com.venta.ports.out.InventarioClientPort;
import com.venta.ports.out.VentasOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public VentasInputPort ventasUseCase(VentasOutputPort ventasOutputPort, InventarioClientPort inventarioClientPort) {
        // Spring Boot se encarga de buscar automáticamente qué componentes implementan
        // VentasOutputPort (MySQLVentasAdapter) e InventarioClientPort (OpenFeign) y los inyecta aquí.
        return new VentasUseCase(ventasOutputPort, inventarioClientPort);
    }
}
