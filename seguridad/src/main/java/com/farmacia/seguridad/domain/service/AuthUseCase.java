package com.farmacia.seguridad.domain.service;

import com.farmacia.seguridad.domain.model.Usuario;
import com.farmacia.seguridad.domain.model.Rol;
import com.farmacia.seguridad.ports.in.SeguridadInputPort;
import com.farmacia.seguridad.ports.out.PasswordEncoderOutputPort;
import com.farmacia.seguridad.ports.out.TokenOutputPort;
import com.farmacia.seguridad.ports.out.UsuarioOutputPort;
import com.farmacia.seguridad.ports.out.NotificacionesClientPort; // <-- NUEVO IMPORT OBLIGATORIO

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthUseCase implements SeguridadInputPort {

    private final UsuarioOutputPort usuarioOutputPort;
    private final TokenOutputPort tokenOutputPort;
    private final PasswordEncoderOutputPort passwordEncoderOutputPort;
    private final NotificacionesClientPort notificacionesClientPort; // <-- NUEVA DEPENDENCIAS INYECTADA

    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_])(?=\\S+$).{7,12}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public AuthUseCase(UsuarioOutputPort usuarioOutputPort,
                       TokenOutputPort tokenOutputPort,
                       PasswordEncoderOutputPort passwordEncoderOutputPort,
                       NotificacionesClientPort notificacionesClientPort) { // <-- INYECTADO EN EL CONSTRUCTOR
        this.usuarioOutputPort = usuarioOutputPort;
        this.tokenOutputPort = tokenOutputPort;
        this.passwordEncoderOutputPort = passwordEncoderOutputPort;
        this.notificacionesClientPort = notificacionesClientPort;
    }

    @Override
    public Usuario registrarUsuario(Usuario usuario, String nombreRol) {
        // -------------------------------------------------------------------------
        // CANDADO 1: CONTROL POR CORREO ELECTRÓNICO EXISTENTE
        // -------------------------------------------------------------------------
        Optional<Usuario> porEmailOpt = usuarioOutputPort.buscarPorEmail(usuario.getEmail().trim());
        if (porEmailOpt.isPresent()) {
            Usuario existente = porEmailOpt.get();
            String rolExistenteNombre = existente.getRol() != null ? existente.getRol().getNombreRol() : "SIN_ROL";

            // NOTIFICACIÓN AUTOMÁTICA EN CALIENTE: Alerta de fraude por duplicación de cuenta
            notificacionesClientPort.enviarAlertaFraude(Map.of(
                    "tipoAlerta", "FRAUDE_DUPLICIDAD_CORREO",
                    "nombreEmpleado", usuario.getNombre(),
                    "emailIntento", usuario.getEmail(),
                    "rolSolicitado", nombreRol.toUpperCase().trim(),
                    "rolExistente", rolExistenteNombre,
                    "fechaHoraIntento", LocalDateTime.now().toString(),
                    "mensajeDetalle", "Se interceptó y bloqueó un intento de registro con un correo electrónico activo."
            ));

            throw new RuntimeException(
                    "REGISTRO BLOQUEADO POR SEGURIDAD: El usuario con el correo electrónico '" + usuario.getEmail() +
                            "' ya se encuentra registrado activamente con el perfil de '" + rolExistenteNombre + "'."
            );
        }

        // -------------------------------------------------------------------------
        // CANDADO 2: EVASIÓN POR CAMBIO DE CORREO (Validación por Nombre de Empleado)
        // -------------------------------------------------------------------------
        Optional<Usuario> porNombreOpt = usuarioOutputPort.buscarPorNombre(usuario.getNombre().trim());
        if (porNombreOpt.isPresent()) {
            Usuario existentePorNombre = porNombreOpt.get();
            String rolOriginal = existentePorNombre.getRol() != null ? existentePorNombre.getRol().getNombreRol() : "SIN_ROL";

            // El bloqueo SOLO se dispara si el rol solicitado es distinto al que ya tiene asignado legítimamente
            if (!rolOriginal.equalsIgnoreCase(nombreRol.trim())) {

                // NOTIFICACIÓN AUTOMÁTICA EN CALIENTE: Alerta de intento de escalado de privilegios
                notificacionesClientPort.enviarAlertaFraude(Map.of(
                        "tipoAlerta", "FRAUDE_ESCALADO_ROLES",
                        "nombreEmpleado", usuario.getNombre(),
                        "emailIntento", usuario.getEmail(),
                        "rolSolicitado", nombreRol.toUpperCase().trim(),
                        "rolOriginalAsignado", rolOriginal,
                        "fechaHoraIntento", LocalDateTime.now().toString(),
                        "mensajeDetalle", "ALERTA CRÍTICA: Intento de suplantación de funciones mediante el uso de un correo alternativo."
                ));

                throw new RuntimeException(
                        "ALERTA DE FRAUDE: El empleado '" + usuario.getNombre() + "' ya está registrado en el sistema " +
                                "con el perfil de '" + rolOriginal + "'. Se ha rechazado el intento de re-registro con el correo '" +
                                usuario.getEmail() + "' para el rol no autorizado de '" + nombreRol.toUpperCase().trim() + "'."
                );
            }
        }

        // Validación estricta de parámetros de contraseña (7-12 caracteres)
        validarComplejidadPassword(usuario.getPassword());

        Rol rolExistente = usuarioOutputPort.buscarRolPorNombre(nombreRol.toUpperCase().trim())
                .orElseThrow(() -> new RuntimeException("El rol seleccionado no es válido. Elija entre PROPIETARIA, FARMACEUTICA o TECNICA."));

        String passwordEncriptada = passwordEncoderOutputPort.encriptar(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        usuario.setRol(rolExistente);

        return usuarioOutputPort.guardarUsuario(usuario);
    }

    private void validarComplejidadPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "Seguridad insuficiente o longitud incorrecta: La contraseña debe tener entre 7 y 12 caracteres, " +
                            "incluir al menos una mayúscula, una minúscula, un número y un carácter especial (@#$%^&+=!-_)."
            );
        }
    }

    @Override
    public String autenticarUsuario(String email, String password) {
        Usuario usuario = usuarioOutputPort.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));

        if (!passwordEncoderOutputPort.verificar(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }

        return tokenOutputPort.generarToken(usuario);
    }

    @Override
    public Map<String, Object> solicitarRecuperacionClave(String email) {
        Usuario usuario = usuarioOutputPort.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("El correo ingresado no pertenece a ningún empleado de la botica."));

        String codigoVerificacion = "FATIFARMA-" + Math.round(Math.random() * 100000);

        // Mantenido intacto y separado del disparador de fraudes del registro comercial
        return Map.of(
                "mensaje", "Código de verificación generado con éxito.",
                "usuario_confirmado", usuario.getNombre(),
                "codigo_auditoria", codigoVerificacion,
                "estado_proceso", "ENVIADO_A_NOTIFICACIONES_SERVICE"
        );
    }
}
