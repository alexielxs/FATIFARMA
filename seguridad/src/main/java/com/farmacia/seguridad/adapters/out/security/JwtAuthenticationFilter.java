package com.farmacia.seguridad.adapters.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY_STRING = "ClaveSecretaSuperFuerteYSeguraParaLaFarmacia2026!";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Validar si la petición trae un Token JWT en la cabecera
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            // Descifrar el token usando la firma secreta
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            String rol = claims.get("rol", String.class); // Extraemos el rol dinámico

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Spring Security exige el prefijo "ROLE_" para validar accesos por rol
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(authority)
                );

                // Guardamos el usuario autenticado en el contexto de Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Si el token expiró o es alterado, no se autentica
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
