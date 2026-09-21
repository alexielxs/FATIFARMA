package com.venta.ports.out;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

// =========================================================================
// PUERTO DE RED COMPLETO: Orquesta la comunicación sincrónica con inventario-service
// =========================================================================
@FeignClient(name = "inventario-service", url = "http://localhost:8082/api/inventario")
public interface InventarioClientPort {

    // HU10: Extrae en tiempo real los datos comerciales de la medicina para el Fraccionamiento (Blíster/Unidades)
    @GetMapping("/lote/{loteId}")
    Map<String, Object> buscarLotePorId(@PathVariable("loteId") Long loteId);

    // HU06: Ordena el descuento físico inmediato de las existencias en MySQL al emitir la boleta
    @PutMapping("/lote/{loteId}/descontar")
    void descontarStockFisico(@PathVariable("loteId") Long loteId, @RequestParam("cantidad") Integer cantidad);
}
