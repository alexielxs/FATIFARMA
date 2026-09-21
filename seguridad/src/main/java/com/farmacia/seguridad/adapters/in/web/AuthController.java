package com.farmacia.seguridad.adapters.in.web;

import com.farmacia.seguridad.domain.model.Usuario;
import com.farmacia.seguridad.domain.service.AuthUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

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

            Usuario guardado = authUseCase.registrarUsuario(nuevoUsuario, rolInput);

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String token = authUseCase.autenticarUsuario(request.get("email"), request.get("password"));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe ingresar un correo electrónico válido."));
            }

            Map<String, Object> resultado = authUseCase.solicitarRecuperacionClave(email);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
