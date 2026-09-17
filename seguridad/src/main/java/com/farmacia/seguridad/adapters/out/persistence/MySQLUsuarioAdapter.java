package com.farmacia.seguridad.adapters.out.persistence;

import com.farmacia.seguridad.domain.model.Usuario;
import com.farmacia.seguridad.domain.model.Rol;
import com.farmacia.seguridad.ports.out.UsuarioOutPutPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class MySQLUsuarioAdapter implements UsuarioOutPutPort {

    private final SpringDataUsuarioRepository repository;

    public MySQLUsuarioAdapter(SpringDataUsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // 1. Convertimos el objeto del Dominio a una Entidad de MySQL asegurando el formato estándar texto
        UsuarioEntity entity = UsuarioEntity.builder()
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .rol(usuario.getRol().name().toUpperCase()) // Guardamos siempre en mayúsculas en la BD
                .build();

        UsuarioEntity guardado = repository.save(entity);

        // 2. Retornamos el objeto convertido de nuevo a Dominio respetando el orden de 5 campos
        return new Usuario(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getEmail(),
                guardado.getPassword(),
                Rol.valueOf(guardado.getRol().toUpperCase().trim()) // Protección contra inconsistencias de texto
        );
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .map(entity -> new Usuario(
                        entity.getId(),
                        entity.getNombre(),
                        entity.getEmail(),
                        entity.getPassword(),
                        Rol.valueOf(entity.getRol().toUpperCase().trim()) // Protección contra inconsistencias de texto
                ));
    }
}
