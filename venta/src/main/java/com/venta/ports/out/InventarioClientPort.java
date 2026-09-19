package com.venta.ports.out;

import java.util.Map;

public interface InventarioClientPort {
    Map<String, Object> buscarLotePorId(Long loteId);
    void descontarStock(Long loteId, Integer cantidad);

}
