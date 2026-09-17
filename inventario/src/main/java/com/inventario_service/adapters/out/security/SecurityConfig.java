package com.inventario_service.adapters.out.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para poder hacer POST/GET desde Postman
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/inventario/**").permitAll() // <-- Abre la puerta pública temporalmente para tus pruebas
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
