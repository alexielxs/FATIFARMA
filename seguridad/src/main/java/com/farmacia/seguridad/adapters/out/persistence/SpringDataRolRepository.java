package com.farmacia.seguridad.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataRolRepository extends JpaRepository<RolEntity, Long> {
    Optional<RolEntity> findByNombreRol(String nombreRol);
}
