package com.notificaciones.ports.out;

import java.util.Map;

public interface InventarioClientPort {
    Map<String, Object> obtenerMetricasDashboard(String sucursal);
}
