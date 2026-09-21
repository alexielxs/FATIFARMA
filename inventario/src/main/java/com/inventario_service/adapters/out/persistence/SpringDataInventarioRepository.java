package com.inventario_service.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpringDataInventarioRepository extends JpaRepository<LoteEntity, Long> {
    List<LoteEntity> findBySucursal_NombreSucursalAndProducto_Categoria_NombreCategoria(String nombreSucursal, String nombreCategoria);
    List<LoteEntity> findBySucursal_NombreSucursalAndJavaFechaVencimientoLessThanEqual(String nombreSucursal, LocalDate fechaLimite);

    @Query("SELECT l FROM LoteEntity l WHERE l.sucursal.nombreSucursal = :sucursal AND l.cantidad <= l.producto.stockMinimo")
    List<LoteEntity> findStockBajo(@Param("sucursal") String sucursal);
}
