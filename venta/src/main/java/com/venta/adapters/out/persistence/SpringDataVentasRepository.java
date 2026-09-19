package com.venta.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataVentasRepository extends JpaRepository<VentaEntity, Long> {}
