package com.notificaciones.adapters.out.client;

import com.notificaciones.ports.out.InventarioClientPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name ="inventario-service", url = "http://localhost:8082/api/inventario")
public interface InventarioFeignClient extends InventarioClientPort {
    @Override
    @GetMapping("/dashboard/metricas")
    Map<String, Object> obtenerMetricasDashboard(@RequestParam("sucursal") String sucursal);

}
