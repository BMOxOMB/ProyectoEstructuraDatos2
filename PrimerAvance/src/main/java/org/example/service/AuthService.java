package org.example.service;

import org.example.DAO.UsuarioDAO;
import org.example.model.Usuario;

public class AuthService {

    private UsuarioDAO usuarioDAO;

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    // LOGIN
    public Usuario login(String username, String password) {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no existe");
        }

        if (!usuario.autenticar(password)) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        return usuario;
    }

    // RECUPERAR CONTRASEÑA (en AuthService.java)
    public void recuperarPassword(String username, String nuevaPassword) {

        Usuario usuario = usuarioDAO.buscarPorUsername(username);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // 1. Cambias la contraseña en el objeto (en memoria)
        usuario.cambiarPassword(nuevaPassword);

        // 2. ¡NUEVO! Guardas el cambio físicamente en la Base de Datos
        usuarioDAO.actualizarPassword(usuario);
    }
}