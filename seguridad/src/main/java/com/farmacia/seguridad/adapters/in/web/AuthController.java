package com.farmacia.seguridad.adapters.in.web;

import com.farmacia.seguridad.domain.model.Usuario;
import com.farmacia.seguridad.domain.model.Rol;
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

            // 1. VALIDACIÓN: Verificar que las contraseñas coincidan
            if (password == null || !password.equals(confirmarPassword)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Las contraseñas ingresadas no coinciden."));
            }

            // 2. VALIDACIÓN: Asegurar que el usuario haya seleccionado un rol obligatorio
            if (rolInput == null || rolInput.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe seleccionar obligatoriamente un rol para el usuario."));
            }

            // Construir el usuario con el rol dinámico seleccionado (Se eliminó la duplicación de rolInput)
            Usuario nuevoUsuario = new Usuario(
                    null,
                    request.get("nombre"),
                    request.get("email"),
                    password,
                    Rol.valueOf(rolInput.toUpperCase().trim()) // Convierte a Enum sin importar espacios o minúsculas
            );

            Usuario guardado = authUseCase.registrar(nuevoUsuario);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Usuario registrado con éxito",
                    "id", guardado.getId(),
                    "nombre", guardado.getNombre(),
                    "email", guardado.getEmail(),
                    "rol", guardado.getRol().name()
            ));
        } catch (IllegalArgumentException e) {
            // Actualizado con tus roles reales de la farmacia
            return ResponseEntity.badRequest().body(Map.of("error", "El rol seleccionado no es válido. Elija entre PROPIETARIO, FARMACEUTICO o TECNICA."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String token = authUseCase.login(request.get("email"), request.get("password"));
            // Devolvemos la llave digital de acceso JWT a la web
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
