package com.inventario_service.ports.out;

import com.inventario_service.domain.model.Lote;
import com.inventario_service.domain.model.Producto;
import java.time.LocalDate;
import java.util.List;

public interface InventarioOutputPort {
    Lote guardarLote(Lote lote);
    List<Lote> buscarPorSucursalYCategoria(String sucursal, String categoria);
    List<Lote> buscarProximosAVencer(String sucursal, LocalDate fechaLimite);
    List<Lote> buscarStockBajo(String sucursal);
}
