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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Inyección de dependencias correcta para el filtro perimetral
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. RUTAS PÚBLICAS: Acceso libre al servicio de autenticación
                        .requestMatchers("/api/auth/login", "/api/auth/registrar").permitAll()

                        // 2. MÓDULO DE INVENTARIO (Sincronizado con tus roles fijos)
                        .requestMatchers("/api/inventario/consultar/**").hasAnyRole("PROPIETARIA", "FARMACEUTICA", "TECNICA")
                        .requestMatchers("/api/inventario/modificar/**", "/api/inventario/lotes/**").hasAnyRole("PROPIETARIA", "FARMACEUTICA")

                        // 3. MÓDULO DE VENTAS (Mapeado exactamente a tus endpoints reales de VentasController)
                        // Cobrar en caja (Momento 1): Acceso para todas
                        .requestMatchers("/api/ventas/procesar").hasAnyRole("PROPIETARIA", "TECNICA")
                        // Adjuntar / Asociar receta posterior (Momento 2): Solo autorizadas por Digemid
                        .requestMatchers("/api/ventas/asociar-receta/**").hasAnyRole("PROPIETARIA", "TECNICA")

                        // 4. MÓDULO DE REPORTES Y ALERTAS (Exclusividades de control superior)
                        .requestMatchers("/api/reportes/financieros/**").hasRole("PROPIETARIA")
                        .requestMatchers("/api/reportes/diarios/**").hasAnyRole("PROPIETARIA", "FARMACEUTICA")

                        // 5. CUALQUIER OTRA RUTA: Candado de seguridad por defecto
                        .anyRequest().authenticated()
                )
                // Inyectamos el filtro de JWT configurado de forma correcta
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
