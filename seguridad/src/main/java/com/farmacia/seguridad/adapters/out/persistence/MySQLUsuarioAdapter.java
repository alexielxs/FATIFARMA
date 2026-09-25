package com.farmacia.seguridad.adapters.out.persistence;

import com.farmacia.seguridad.domain.model.Usuario;
import com.farmacia.seguridad.domain.model.Rol;
import com.farmacia.seguridad.ports.out.UsuarioOutputPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class MySQLUsuarioAdapter implements UsuarioOutputPort {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataRolRepository rolRepository;

    public MySQLUsuarioAdapter(SpringDataUsuarioRepository usuarioRepository, SpringDataRolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        RolEntity rolEntity = rolRepository.findByNombreRol(usuario.getRol().getNombreRol())
                .orElseThrow(() -> new RuntimeException("Error: El rol relacional no existe en MySQL."));

        UsuarioEntity entity = UsuarioEntity.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .rol(rolEntity)
                .build();

        UsuarioEntity guardado = usuarioRepository.save(entity);
        return mapearADominio(guardado);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(this::mapearADominio);
    }

    @Override
    public Optional<Rol> buscarRolPorNombre(String nombreRol) {
        return rolRepository.findByNombreRol(nombreRol)
                .map(entity -> new Rol(entity.getId(), entity.getNombreRol()));
    }

    // =========================================================================
    // IMPLEMENTACIÓN DEL PUERTO PARA EL CANDADO CONTRA EL FRAUDE DE ROLES
    // =========================================================================
    @Override
    public Optional<Usuario> buscarPorNombre(String nombre) {
        // Busca en la base de datos relacional y realiza el mapeo seguro al dominio
        return usuarioRepository.findByNombre(nombre).map(this::mapearADominio);
    }

    private Usuario mapearADominio(UsuarioEntity entity) {
        if (entity == null) return null;

        Rol rolDominio = entity.getRol() != null
                ? new Rol(entity.getRol().getId(), entity.getRol().getNombreRol())
                : null;

        return Usuario.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .rol(rolDominio)
                .build();
    }
}
