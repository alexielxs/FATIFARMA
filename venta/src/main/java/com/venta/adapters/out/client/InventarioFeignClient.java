package com.venta.adapters.out.client;

import com.venta.ports.out.InventarioClientPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "inventario-service", url = "http://localhost:8082/api/inventario")
public interface InventarioFeignClient extends InventarioClientPort {

    @Override
    @GetMapping("/lote/{loteId}")
    Map<String, Object> buscarLotePorId(@PathVariable("loteId") Long loteId);

    @Override
    @PutMapping("/lote/{loteId}/descontar")
    void descontarStock(@PathVariable("loteId") Long loteId, @RequestParam("cantidad") Integer cantidad);
}
