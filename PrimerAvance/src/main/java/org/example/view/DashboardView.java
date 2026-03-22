package org.example.view;

import org.example.model.Grafo;
import org.example.controller.UsuarioController;
import javax.swing.*;
import java.awt.*;

public class DashboardView extends JPanel {

    public DashboardView(NavigationManager nav, Grafo grafo, UsuarioController userController) {
        // Uso de BorderLayout para organizar la pantalla
        setLayout(new BorderLayout());

        // Cabecera
        JLabel titulo = new JLabel("Red Social - Visualización de Grafo", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Central: El dibujo interactivo del grafo
        // GrafoView debe manejar los eventos de click izquierdo y derecho
        GrafoView grafoView = new GrafoView(grafo);

        // Panel Lateral de Acciones
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createTitledBorder("Acciones"));

        JButton btnNuevoGrupo = new JButton("Agregar Grupo");
        JButton btnVerDetalle = new JButton("Ver Mi Perfil"); // Para ver amigos y sugerencias BFS
        JButton btnLogout = new JButton("Cerrar Sesión");

        // Estilo de botones
        btnNuevoGrupo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerDetalle.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Lógica de botones
        btnNuevoGrupo.addActionListener(e -> {
            // Aquí se llama a un diálogo para ingresar Nombre y Color del grupo
            JOptionPane.showMessageDialog(this, "Función para crear grupos");
        });

        btnVerDetalle.addActionListener(e -> {
            // Navega a la vista que muestra la tabla de amigos y sugerencias
            // nav.goToPerfil();
        });

        btnLogout.addActionListener(e -> nav.goToLogin());

        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(btnNuevoGrupo);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(btnVerDetalle);
        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(btnLogout);

        // Ensamblado de la vista
        add(titulo, BorderLayout.NORTH);
        add(grafoView, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
    }
}