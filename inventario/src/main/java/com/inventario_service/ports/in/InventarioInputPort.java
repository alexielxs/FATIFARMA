package com.inventario_service.ports.in;

import com.inventario_service.domain.model.Categoria;
import com.inventario_service.domain.model.Lote;
import com.inventario_service.domain.model.Producto;
import com.inventario_service.domain.model.Sucursal;
import java.util.List;
import java.util.Map;

public interface InventarioInputPort {
    Lote ingresarNuevoLote(Lote lote, Producto producto, String nombreCategoria); // HU04, HU05
    void actualizarStockPorVenta(Long loteId, Integer cantidadVendida); // HU06
    Lote modificarDatosInventario(Long loteId, Map<String, Object> actualizaciones); // HU07
    String consultarUbicacionFisica(Long loteId); // HU08
    List<Lote> listarCatalogoPorFiltros(String sucursal, String categoria);
    List<Lote> obtenerTodosLosLotesParaAlertas();
}

