package com.farmacia.seguridad.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);
    // Habilita la búsqueda exacta por nombre de empleado en MySQL para mitigar
    // evasiones de roles mediante el uso de correos alternativos.
    Optional<UsuarioEntity> findByNombre(String nombre);
}
