package com.venta.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringDataVentasRepository extends JpaRepository<VentaEntity, Long> {

    // 1. HU11: Historial - Filtrar todas las boletas registradas en una sucursal específica por rango de fechas (Cierre de Caja)
    List<VentaEntity> findBySucursalAndFechaVentaBetween(String sucursal, LocalDateTime inicio, LocalDateTime fin);

    // 2. HU12: Comparativas - Agrupar y totalizar los ingresos económicos de la farmacia según el método de pago (Yape, Efectivo)
    @Query("SELECT v.metodoPago, SUM(v.total) FROM VentaEntity v WHERE v.sucursal = :sucursal AND v.fechaVenta BETWEEN :inicio AND :fin GROUP BY v.metodoPago")
    List<Object[]> calcularTotalesPorMetodoPago(
            @Param("sucursal") String sucursal,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}
