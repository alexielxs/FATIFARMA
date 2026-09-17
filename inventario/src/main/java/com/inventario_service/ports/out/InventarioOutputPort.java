package com.inventario_service.ports.out;

import com.inventario_service.domain.model.Categoria;
import com.inventario_service.domain.model.Lote;
import com.inventario_service.domain.model.Sucursal;

import java.time.LocalDate;
import java.util.List;

public interface InventarioOutputPort {
    Lote guardarLote(Lote lote);
    List<Lote> buscarPorSucursalYCategoria(Sucursal sucursal, Categoria categoria);
    List<Lote> buscarProximosAVencer(Sucursal sucursal, LocalDate fechaLimite);
    List<Lote> buscarStockBajo(Sucursal sucursal);
}
