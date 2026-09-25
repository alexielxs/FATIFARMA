package com.farmacia.seguridad.adapters.in.web;

import com.farmacia.seguridad.domain.model.Usuario;
import com.farmacia.seguridad.ports.in.SeguridadInputPort; // <-- CORREGIDO: Importación del Puerto de Entrada
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SeguridadInputPort seguridadInputPort; // <-- CORREGIDO: Inyección desacoplada al Puerto

    public AuthController(SeguridadInputPort seguridadInputPort) {
        this.seguridadInputPort = seguridadInputPort;
    }

    // =========================================================================
    // REGISTRO DE USUARIOS POR ROL (Sincronizado con Postman)
    // =========================================================================
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            String confirmarPassword = request.get("confirmarPassword");
            String rolInput = request.get("rol");

            if (password == null || !password.equals(confirmarPassword)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Las contraseñas ingresadas no coinciden."));
            }

            if (rolInput == null || rolInput.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe seleccionar obligatoriamente un rol para el usuario."));
            }

            Usuario nuevoUsuario = new Usuario(null, request.get("nombre"), request.get("email"), password, null);

            // Llamada a través del puerto de entrada
            Usuario guardado = seguridadInputPort.registrarUsuario(nuevoUsuario, rolInput);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Usuario registrado con éxito en SIGIFARM",
                    "id", guardado.getId(),
                    "nombre", guardado.getNombre(),
                    "email", guardado.getEmail(),
                    "rol", guardado.getRol() != null ? guardado.getRol().getNombreRol() : rolInput.toUpperCase().trim()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // INICIO DE SESIÓN (HU02 - Generación del Bearer Token)
    // =========================================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            // Llamada a través del puerto de entrada
            String token = seguridadInputPort.autenticarUsuario(request.get("email"), request.get("password"));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // SOLICITUD DE RECUPERACIÓN DE CREDENCIALES (Trazabilidad)
    // =========================================================================
    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe ingresar un correo electrónico válido."));
            }

            // Llamada a través del puerto de entrada
            Map<String, Object> resultado = seguridadInputPort.solicitarRecuperacionClave(email);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
