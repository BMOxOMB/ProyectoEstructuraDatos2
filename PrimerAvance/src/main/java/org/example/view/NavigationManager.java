package org.example.view;

import org.example.controller.AuthController;
import org.example.controller.UsuarioController;
import org.example.DAO.UsuarioDAO;
import org.example.model.Grafo;
import org.example.model.Usuario;
import javax.swing.*;
import java.util.List;

public class NavigationManager {
    private final JFrame frame;
    private final AuthController authController;
    private final UsuarioController usuarioController;
    private final UsuarioDAO usuarioDAO;
    private final Grafo grafo;
    private Usuario usuarioLogueado;

    public NavigationManager(JFrame frame, AuthController auth, UsuarioController usuario, UsuarioDAO dao, Grafo grafo) {
        this.frame = frame;
        this.authController = auth;
        this.usuarioController = usuario;
        this.usuarioDAO = dao;
        this.grafo = grafo;
    }

    // --- Gestión de Sesión ---
    public void setUsuarioLogueado(Usuario u) {
        this.usuarioLogueado = u;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    // --- Navegación ---
    public void goToLogin() {
        this.usuarioLogueado = null;
        changeView(new LoginView(this, authController, grafo));
    }

    public void goToPerfil(Usuario usuario) {
        // Al entrar al perfil, cargamos los intereses frescos de la DB
        usuarioController.cargarInteresesDelUsuario(usuario);
        changeView(new PerfilView(this, usuario, usuarioController));
    }

    public void goToAmistades() {
        // Pasamos el grafo completo y el controlador para gestionar las relaciones
        changeView(new AmistadesView(this, grafo, usuarioController));
    }

    public void goToRegistro() {
        changeView(new RegistroView(this, usuarioController, authController, grafo));
    }

    public void goToRecuperar() {
        changeView(new RecuperarPasswordView(this, authController, grafo));
    }

    public void goToDashboard() {
        // Sincronizamos el grafo antes de entrar para ver cambios recientes
        prepararGrafo();
        changeView(new DashboardView(this, grafo, usuarioController));
    }

    /**
     * Sincroniza la estructura del Grafo en memoria con la Base de Datos.
     */
    public void prepararGrafo() {
        // 1. Limpiar para evitar duplicados visuales
        this.grafo.getAdjList().clear();

        // 2. Cargar todos los nodos (Usuarios)
        List<Usuario> desdeDB = usuarioDAO.obtenerTodos();
        for (Usuario u : desdeDB) {
            this.grafo.agregarUsuario(u);
        }

        // 3. Cargar todas las aristas (Relaciones de Amistad)
        usuarioDAO.cargarAmistadesEnGrafo(this.grafo);

        System.out.println("✅ Red sincronizada: " + desdeDB.size() + " nodos listos.");
    }

    /**
     * Método centralizado para cambiar de pantalla con limpieza de UI.
     */
    private void changeView(JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    public UsuarioController getUsuarioController() {
        return this.usuarioController;
    }
}