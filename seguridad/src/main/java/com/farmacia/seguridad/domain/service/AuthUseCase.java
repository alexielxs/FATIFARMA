package com.farmacia.seguridad.domain.service;

import com.farmacia.seguridad.domain.model.Usuario;
import com.farmacia.seguridad.domain.model.Rol;
import com.farmacia.seguridad.ports.in.SeguridadInputPort;
import com.farmacia.seguridad.ports.out.PasswordEncoderOutputPort;
import com.farmacia.seguridad.ports.out.TokenOutputPort;
import com.farmacia.seguridad.ports.out.UsuarioOutPutPort;

import java.util.Map;

public class AuthUseCase implements SeguridadInputPort {

    private final UsuarioOutPutPort usuarioOutPutPort;
    private final TokenOutputPort tokenOutputPort;
    private final PasswordEncoderOutputPort passwordEncoderOutputPort;

    public AuthUseCase(UsuarioOutPutPort usuarioOutPutPort,
                       TokenOutputPort tokenOutputPort,
                       PasswordEncoderOutputPort passwordEncoderOutputPort) {
        this.usuarioOutPutPort = usuarioOutPutPort;
        this.tokenOutputPort = tokenOutputPort;
        this.passwordEncoderOutputPort = passwordEncoderOutputPort;
    }

    @Override
    public Usuario registrarUsuario(Usuario usuario, String nombreRol) {
        if (usuarioOutPutPort.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electronico ya esta registrado en el sistema SIGIFARM.");
        }

        Rol rolExistente = usuarioOutPutPort.buscarRolPorNombre(nombreRol.toUpperCase().trim())
                .orElseThrow(() -> new RuntimeException("El rol seleccionado no es válido. Elija entre PROPIETARIO, FARMACEUTICO o TECNICA."));

        String passwordEncriptada = passwordEncoderOutputPort.encriptar(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        usuario.setRol(rolExistente);

        return usuarioOutPutPort.guardarUsuario(usuario);
    }

    @Override
    public String autenticarUsuario(String email, String password) {
        Usuario usuario = usuarioOutPutPort.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));

        if (!passwordEncoderOutputPort.verificar(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }

        return tokenOutputPort.generarToken(usuario);
    }

    @Override
    public Map<String, Object> solicitarRecuperacionClave(String email) {
        Usuario usuario = usuarioOutPutPort.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("El correo ingresado no pertenece a ningún empleado de la botica."));

        String codigoVerificacion = "FATIFARMA-" + Math.round(Math.random() * 100000);

        return Map.of(
                "mensaje", "Código de verificación generado con éxito.",
                "usuario_confirmado", usuario.getNombre(),
                "codigo_auditoria", codigoVerificacion,
                "estado_proceso", "ENVIADO_A_NOTIFICACIONES_SERVICE"
        );
    }
}
