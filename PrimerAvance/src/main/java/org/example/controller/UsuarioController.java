package org.example.controller;

import org.example.DAO.UsuarioDAO;
import org.example.DAO.InteresDAO;
import org.example.model.Usuario;
import org.example.service.RedSocialService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;
    private final RedSocialService service;
    private final InteresDAO interesDAO; // <--- Agregado para persistencia de intereses

    // Actualizamos el constructor para recibir el InteresDAO
    public UsuarioController(UsuarioDAO dao, RedSocialService service, InteresDAO interesDAO) {
        this.usuarioDAO = dao;
        this.service = service;
        this.interesDAO = interesDAO;
    }

    public void crearUsuario(
            String username, String password, String nombre,
            String apellido1, String apellido2,
            LocalDate fechaNacimiento, String avatar
    ) {
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

        Usuario usuario = new Usuario(
                username, password, nombre,
                apellido1, apellido2,
                fechaNacimiento, avatar
        );

        usuarioDAO.guardar(usuario);
        service.agregarUsuario(usuario);
    }

    public void asignarGrupoAUsuario(String username, String grupo, String color) {
        Usuario u = usuarioDAO.buscarPorUsername(username);
        if (u != null) {
            u.setGrupo(grupo, color);
        }
    }

    /**
     * Obtiene todos los intereses disponibles en la tabla catalogo_intereses
     * Útil para llenar el JComboBox en el PerfilView.
     */
    public List<String> getCatalogoCompleto() {
        return interesDAO.obtenerTodosLosIntereses();
    }

    /**
     * Agrega un interés tanto a la base de datos como al objeto en memoria
     */
    public void agregarInteres(Usuario usuario, String nuevoInteres) {
        if (nuevoInteres != null && !nuevoInteres.trim().isEmpty()) {
            // 1. Persistencia real en MySQL
            interesDAO.guardarInteresUsuario(usuario.getUsername(), nuevoInteres.trim());

            // 2. Sincronización con el modelo en memoria para el Grafo
            if (usuario.getIntereses() == null) {
                usuario.setIntereses(new ArrayList<>());
            }
            if (!usuario.getIntereses().contains(nuevoInteres.trim())) {
                usuario.addInteres(nuevoInteres.trim());
            }
        }
    }

    /**
     * Elimina un interés de la base de datos y del objeto en memoria
     */
    public void eliminarInteres(Usuario usuario, String interes) {
        if (usuario.getIntereses() != null && interes != null) {
            // 1. Eliminar de MySQL
            interesDAO.eliminarInteresUsuario(usuario.getUsername(), interes);

            // 2. Eliminar del objeto Usuario
            usuario.removeInteres(interes);
        }
    }

    public Usuario buscar(String username) {
        return usuarioDAO.buscarPorUsername(username);
    }
}