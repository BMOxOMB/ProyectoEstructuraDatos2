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
    private final InteresDAO interesDAO;


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

    /**
     * Crea una relación de amistad en DB y en el Grafo (Service)
     */
    public void agregarAmistad(Usuario u1, Usuario u2) {
        if (u1 == null || u2 == null) return;

        try {
            // 1. Guardar en la tabla relacional de MySQL (DAO)
            usuarioDAO.guardarAmistad(u1.getUsername(), u2.getUsername());

            // 2. Reflejar en la estructura de datos en memoria (Service/Grafo)
            service.conectarUsuarios(u1, u2);

            System.out.println("✅ Amistad establecida entre " + u1.getUsername() + " y " + u2.getUsername());
        } catch (Exception e) {
            System.err.println("❌ Error al agregar amistad: " + e.getMessage());
        }
    }

    /**
     * Elimina la relación de amistad en DB y en el Grafo
     */
    public void eliminarAmistad(Usuario u1, Usuario u2) {
        if (u1 == null || u2 == null) return;

        try {
            // 1. Eliminar de MySQL
            usuarioDAO.eliminarAmistad(u1.getUsername(), u2.getUsername());

            // 2. Romper la conexión en el Grafo (Service)
            service.desconectarUsuarios(u1, u2);

            System.out.println("🗑️ Amistad eliminada entre " + u1.getUsername() + " y " + u2.getUsername());
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar amistad: " + e.getMessage());
        }
    }

    public List<Usuario> obtenerSugerenciasAmistad(Usuario usuario) {
        return service.recomendarPorIntereses(usuario);
    }

    public Usuario buscar(String username) {
        // 1. Buscamos los datos básicos del usuario
        Usuario u = usuarioDAO.buscarPorUsername(username);

        // 2. Si el usuario existe, cargamos sus intereses desde la DB
        if (u != null) {
            List<String> interesesDesdeDB = interesDAO.obtenerInteresesUsuario(username);
            u.setIntereses(interesesDesdeDB);
            System.out.println("✅ Intereses cargados para " + username + ": " + interesesDesdeDB);
        }

        return u;
    }

    public void cargarInteresesDelUsuario(Usuario usuario) {
        if (usuario != null) {
            // Consultamos al DAO por la lista real de la base de datos
            List<String> desdeDB = interesDAO.obtenerInteresesUsuario(usuario.getUsername());

            // Limpiamos los intereses actuales del objeto y cargamos los de la DB
            usuario.setIntereses(desdeDB);

            System.out.println("DEBUG: Intereses cargados para " + usuario.getUsername() + ": " + desdeDB.size());
        }
    }
}