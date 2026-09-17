package com.inventario_service.ports.in;

import com.inventario_service.domain.model.Categoria;
import com.inventario_service.domain.model.Lote;
import com.inventario_service.domain.model.Sucursal;

import java.util.List;
import java.util.Map;

public interface InventarioInputPort {
    Lote registrarIngresoLote(Lote lote);
    List<Lote> listarInventarioPorSucursalYCategoria(Sucursal sucursal, Categoria categoria);
    Map<String, Object> obtenerMetricasDashboard(Sucursal sucursal);
}
