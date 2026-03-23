package org.example;

import org.example.DAO.InteresDAO;
import org.example.DAO.UsuarioDAO;
import org.example.controller.AuthController;
import org.example.controller.UsuarioController;
import org.example.model.Grafo;
import org.example.service.AuthService;
import org.example.service.RedSocialService;
import org.example.view.NavigationManager;
// Importa tu clase de conexión
import org.example.database.ConexionDB;

import javax.swing.*;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Obtener conexión desde tu clase existente
                Connection conn = ConexionDB.getConexion();

                // 2. Persistencia (DAOs)
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                // Pasamos la conexión al InteresDAO para las consultas de catálogo
                InteresDAO interesDAO = new InteresDAO(conn);

                // 3. Estructura de Datos (Grafo)
                Grafo grafo = new Grafo();

                // 4. Servicios (Lógica de Negocio)
                RedSocialService redSocialService = new RedSocialService(grafo);
                AuthService authService = new AuthService(usuarioDAO);

                // 5. Controladores (Intermediarios)
                // Se pasan los 3 argumentos: UsuarioDAO, RedSocialService e InteresDAO
                UsuarioController usuarioController = new UsuarioController(usuarioDAO, redSocialService, interesDAO);
                AuthController authController = new AuthController(authService);

                // 6. Ventana Principal
                JFrame frame = new JFrame("Cenfotec Connect - Red Social");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Tamaño inicial (el Dashboard luego lo cambiará a 1000x700)
                frame.setSize(400, 400);

                // 7. Inicializar el Manager de Navegación
                NavigationManager nav = new NavigationManager(
                        frame,
                        authController,
                        usuarioController,
                        usuarioDAO,
                        grafo
                );

                // 8. Arrancar la aplicación en la pantalla de Login
                nav.goToLogin();

                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al iniciar la aplicación: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}