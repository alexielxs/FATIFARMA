package com.inventario_service.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpringDataSucursalRepository extends JpaRepository<SucursalEntity, Long> {
    Optional<SucursalEntity> findByNombreSucursal(String nombreSucursal);
}
