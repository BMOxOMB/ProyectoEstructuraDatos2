package org.example;

import org.example.controller.AuthController;
import org.example.controller.UsuarioController;
import org.example.DAO.UsuarioDAO;
import org.example.model.Grafo;
import org.example.service.AuthService;
import org.example.service.RedSocialService;
import org.example.view.LoginView;
import org.example.view.NavigationManager;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // DAO
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // GRAFO
            Grafo grafo = new Grafo();

            // SERVICES
            RedSocialService redSocialService = new RedSocialService(grafo);
            AuthService authService = new AuthService(usuarioDAO);

            // CONTROLLERS
            UsuarioController usuarioController = new UsuarioController(usuarioDAO, redSocialService);
            AuthController authController = new AuthController(authService);

            // FRAME
            JFrame frame = new JFrame("Mini Red Social");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);

            // Inicializar el Manager
            NavigationManager nav = new NavigationManager(frame, authController, usuarioController, grafo);

            // Iniciamos la aplicación llamando al método del Manager
            nav.goToLogin();

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}