package com.inventario_service.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface SpringDataInventarioRepository extends JpaRepository<LoteEntity, Long> {

    // 1. Filtrar inventario por farmacia y categoría para el catálogo de pestañas
    List<LoteEntity> findBySucursalAndProducto_Categoria(String sucursal, String categoria);

    // 2. Alerta del Prototipo: Buscar lotes que venzan antes de una fecha límite en una farmacia específica
    List<LoteEntity> findBySucursalAndFechaVencimientoLessThanEqual(String sucursal, LocalDate fechaLimite);

    // 3. Alerta del Prototipo: Buscar lotes cuya cantidad sea menor o igual al stock mínimo configurado en su producto
    @Query("SELECT l FROM LoteEntity l WHERE l.sucursal = :sucursal AND l.cantidad <= l.producto.stockMinimo")
    List<LoteEntity> findStockBajo(@Param("sucursal") String sucursal);
}
