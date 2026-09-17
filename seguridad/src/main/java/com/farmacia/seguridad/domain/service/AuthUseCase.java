package com.farmacia.seguridad.domain.service;

import com.farmacia.seguridad.domain.model.Usuario;
import com.farmacia.seguridad.ports.in.AuthInpurtPort;
import com.farmacia.seguridad.ports.out.PasswordEncoderOutputPort;
import com.farmacia.seguridad.ports.out.TokenOutputPort;
import com.farmacia.seguridad.ports.out.UsuarioOutPutPort;

public class AuthUseCase implements AuthInpurtPort {
    private final UsuarioOutPutPort usuarioOutPutPort;
    private final TokenOutputPort tokenOutputPort;
    private final PasswordEncoderOutputPort passwordEncoderOutputPort;

    public AuthUseCase(UsuarioOutPutPort usuarioOutPutPort, TokenOutputPort tokenOutputPort, PasswordEncoderOutputPort passwordEncoderOutputPort) {
        this.usuarioOutPutPort = usuarioOutPutPort;
        this.tokenOutputPort = tokenOutputPort;
        this.passwordEncoderOutputPort = passwordEncoderOutputPort;
    }

    @Override
    public Usuario registrar(Usuario usuario){
        if (usuarioOutPutPort.buscarPorEmail(usuario.getEmail()).isPresent()){
            throw new RuntimeException("El correo electronico ya esta registrado");
        }
        String passwordEncriptada = passwordEncoderOutputPort.encriptar(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        return usuarioOutPutPort.guardar(usuario);
    }
    @Override
    public String login(String email, String password){
        Usuario usuario = usuarioOutPutPort.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));
        if(!passwordEncoderOutputPort.verificar(password, usuario.getPassword())){
            throw new RuntimeException("Credenciales invalidas");
        }
        return tokenOutputPort.generarToken(usuario);
    }
}
