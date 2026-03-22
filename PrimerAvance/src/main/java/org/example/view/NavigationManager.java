package org.example.view;

import org.example.controller.AuthController;
import org.example.controller.UsuarioController;
import org.example.model.Grafo;
import javax.swing.*;

public class NavigationManager {
    private final JFrame frame;
    private final AuthController authController;
    private final UsuarioController usuarioController;
    private final Grafo grafo;

    public NavigationManager(JFrame frame, AuthController auth, UsuarioController usuario, Grafo grafo) {
        this.frame = frame;
        this.authController = auth;
        this.usuarioController = usuario;
        this.grafo = grafo;
    }

    public void goToLogin() {
        changeView(new LoginView(this, authController, grafo));
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

    private void changeView(JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}