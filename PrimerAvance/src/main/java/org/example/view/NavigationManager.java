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
    private final UsuarioDAO usuarioDAO; // Agregado para cargar la DB
    private final Grafo grafo;
    private Usuario usuarioLogueado; // Para saber quién está en la sesión

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
        this.usuarioLogueado = null; // Limpiar sesión al salir
        changeView(new LoginView(this, authController, grafo));
    }

    public void goToPerfil(Usuario usuario) {
        // Creamos la nueva vista pasando las dependencias necesarias
        PerfilView perfilView = new PerfilView(this, usuario, usuarioController);

        // Intercambiamos el contenido del frame
        frame.setContentPane(perfilView);

        // Forzamos a Swing a redibujar la interfaz para evitar "fantasmas"
        frame.revalidate();
        frame.repaint();
    }

    public void goToRegistro() {
        changeView(new RegistroView(this, usuarioController, authController, grafo));
    }

    public void goToRecuperar() {
        changeView(new RecuperarPasswordView(this, authController, grafo));
    }

    public void goToDashboard() {
        changeView(new DashboardView(this, grafo, usuarioController));
    }


    public void prepararGrafo() {
        // 1. Limpiar el grafo actual para evitar duplicados en memoria
        this.grafo.getAdjList().clear();

        // 2. Obtener usuarios de la DB
        List<Usuario> desdeDB = usuarioDAO.obtenerTodos();

        // 3. Insertar nodos en la estructura de Red
        for (Usuario u : desdeDB) {
            this.grafo.agregarUsuario(u);
        }

        // 4. Cargar las aristas (Amistades) desde la tabla relacional
        // Nota: Asegúrate que tu UsuarioDAO tenga este método implementado
        usuarioDAO.cargarAmistadesEnGrafo(this.grafo);

        System.out.println("✅ Estructura de Red sincronizada: " + desdeDB.size() + " usuarios cargados.");
    }

    private void changeView(JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}