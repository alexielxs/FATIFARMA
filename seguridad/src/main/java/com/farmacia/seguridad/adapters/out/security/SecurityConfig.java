package com.farmacia.seguridad.adapters.out.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. RUTAS PÚBLICAS: Cualquiera puede loguearse o registrarse
                        .requestMatchers("/api/auth/login", "/api/auth/registrar").permitAll()

                        // 2. MÓDULO DE INVENTARIO (Rutas de ejemplo para cuando crees el microservicio de Inventario)
                        .requestMatchers("/api/inventario/consultar/**").hasAnyRole("PROPIETARIO", "FARMACEUTICO", "TECNICA")
                        .requestMatchers("/api/inventario/modificar/**", "/api/inventario/lotes/**").hasAnyRole("PROPIETARIO", "FARMACEUTICO")

                        // 3. MÓDULO DE VENTAS (Rutas de ejemplo para el microservicio de Ventas)
                        .requestMatchers("/api/ventas/crear/**").hasAnyRole("PROPIETARIO", "FARMACEUTICO", "TECNICA")
                        .requestMatchers("/api/ventas/auditar/**").hasRole("PROPIETARIO")

                        // 4. MÓDULO DE REPORTES (Rutas de ejemplo para el microservicio de Reportes)
                        .requestMatchers("/api/reportes/financieros/**").hasRole("PROPIETARIO")
                        .requestMatchers("/api/reportes/diarios/**").hasAnyRole("PROPIETARIO", "FARMACEUTICO")

                        // 5. CUALQUIER OTRA RUTA: Requiere que el usuario esté logueado
                        .anyRequest().authenticated()
                )
                // Inyectamos nuestro filtro de JWT antes del filtro de autenticación por defecto de Spring
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
