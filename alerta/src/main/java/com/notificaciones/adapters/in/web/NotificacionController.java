package com.notificaciones.adapters.in.web;

import com.notificaciones.domain.service.NotificacionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*") // Habilita que el Frontend jale los datos desde cualquier puerto sin errores de CORS
public class NotificacionController {

    private final NotificacionUseCase notificacionUseCase;

    public NotificacionController(NotificacionUseCase notificacionUseCase) {
        this.notificacionUseCase = notificacionUseCase;
    }

    // ENDPOINT PARA LAS TABLAS: Jala y procesa las alertas de caducidad y reposición
    @GetMapping("/alertas")
    public ResponseEntity<?> obtenerReporteAlertas(@RequestParam String sucursal) {
        try {
            Map<String, Object> reporte = notificacionUseCase.generarReporteAlertas(sucursal.toUpperCase().trim());
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}