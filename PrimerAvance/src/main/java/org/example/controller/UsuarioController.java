package org.example.controller;

import org.example.DAO.UsuarioDAO;
import org.example.model.Usuario;
import org.example.service.RedSocialService;
import java.time.LocalDate;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;
    private final RedSocialService service;

    public UsuarioController(UsuarioDAO dao, RedSocialService service) {
        this.usuarioDAO = dao;
        this.service = service;
    }

    public void crearUsuario(
            String username, String password, String nombre,
            String apellido1, String apellido2,
            LocalDate fechaNacimiento, String avatar
    ) {
        // --- Validaciones de Reglas de Negocio (Rúbrica: Control de Errores) ---
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username obligatorio");

        if (password == null || password.length() < 4)
            throw new IllegalArgumentException("Contraseña mínima 4 caracteres");

        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("Nombre obligatorio");

        if (fechaNacimiento == null)
            throw new IllegalArgumentException("Fecha de nacimiento requerida");

        if (fechaNacimiento.isAfter(LocalDate.now().minusYears(10)))
            throw new IllegalArgumentException("Debe tener al menos 10 años para unirse");

        if (usuarioDAO.buscarPorUsername(username) != null)
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");

        // --- Creación del Objeto ---
        Usuario usuario = new Usuario(
                username, password, nombre,
                apellido1, apellido2,
                fechaNacimiento, avatar
        );

        // --- Persistencia y Lógica de Red ---
        usuarioDAO.guardar(usuario);      // Guardar en "Base de Datos" (DAO)
        service.agregarUsuario(usuario);  // Agregar al Grafo (Estructura de Red)
    }

    // Método para Gestión de Grupos desde la interfaz
    public void asignarGrupoAUsuario(String username, String grupo, String color) {
        Usuario u = usuarioDAO.buscarPorUsername(username);
        if (u != null) {
            u.setGrupo(grupo, color);
            // El service/grafo se actualiza automáticamente al ser la misma referencia
        }
    }

    public Usuario buscar(String username) {
        return usuarioDAO.buscarPorUsername(username);
    }
}