package org.example.view;

import org.example.model.Grafo;
import org.example.controller.UsuarioController;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JPanel {

    public DashboardView(NavigationManager nav, Grafo grafo, UsuarioController userController) {
        // 1. AJUSTE DE VENTANA: Cambia el tamaño al ingresar
        SwingUtilities.invokeLater(() -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame frame) {
                frame.setSize(1500, 750);
                frame.setLocationRelativeTo(null); // Centra la ventana en pantalla
            }
        });

        setLayout(new BorderLayout());

        // 2. CABECERA
        JLabel titulo = new JLabel("Red Social - Visualización de Grafo", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(245, 245, 245));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // 3. CENTRO: GrafoView con Scroll
        GrafoView grafoView = new GrafoView(grafo,nav);
        JScrollPane scrollPane = new JScrollPane(grafoView);

        // Desactivar optimización Blit para evitar rastros visuales
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        // Velocidad de scroll cómoda
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        // 4. LATERAL: Menú de Acciones Sólido
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(200, 0));
        sidePanel.setBackground(new Color(235, 235, 235));
        sidePanel.setOpaque(true);
        sidePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        // Botones de acción
        JButton btnNuevoGrupo = new JButton("Agregar Grupo");
        JButton btnVerDetalle = new JButton("Ver Mi Perfil");
        JButton btnLogout = new JButton("Cerrar Sesión");

        btnNuevoGrupo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerDetalle.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension btnSize = new Dimension(160, 35);
        btnNuevoGrupo.setMaximumSize(btnSize);
        btnVerDetalle.setMaximumSize(btnSize);
        btnLogout.setMaximumSize(btnSize);

        // Listeners
        btnLogout.addActionListener(e -> nav.goToLogin());
        btnVerDetalle.addActionListener(e -> {
            // nav.goToPerfil();
        });

        btnVerDetalle.addActionListener(e -> {
            Usuario logueado = nav.getUsuarioLogueado();
            if (logueado != null) {
                nav.goToPerfil(logueado);
            }
        });

        JLabel lblMenu = new JLabel("ACCIONES");
        lblMenu.setFont(new Font("Arial", Font.BOLD, 12));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(lblMenu);
        sidePanel.add(Box.createVerticalStrut(25));
        sidePanel.add(btnNuevoGrupo);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(btnVerDetalle);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(btnLogout);

        // Ensamblado
        add(titulo, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        // Asegurar que el menú esté siempre al frente
        setComponentZOrder(sidePanel, 0);
    }
}