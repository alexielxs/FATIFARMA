package com.farmacia.seguridad.adapters.out.security;

import com.farmacia.seguridad.domain.model.Usuario; // <-- Importación del modelo de dominio
import com.farmacia.seguridad.ports.out.TokenOutputPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenAdapter implements TokenOutputPort {

    private static final String SECRET_KEY_STRING = "ClaveSecretaSuperFuerteYSeguraParaLaFarmacia2026!";
    private static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generarToken(Usuario usuario) {
        // Si el rol o su nombre es nulo, viaja como null en los claims sin forzar ninguna cadena por defecto.
        String nombreRolStr = (usuario.getRol() != null) ? usuario.getRol().getNombreRol() : null;

        Map<String, Object> extraClaims = Map.of(
                "rol", nombreRolStr != null ? nombreRolStr.toUpperCase().trim() : "",
                "id_usuario", usuario.getId()
        );

        return Jwts.builder()
                .claims(extraClaims)
                .subject(usuario.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }
}
