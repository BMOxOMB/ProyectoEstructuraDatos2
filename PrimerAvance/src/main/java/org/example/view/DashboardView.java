package org.example.view;

import org.example.model.Grafo;
import org.example.controller.UsuarioController;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardView extends JPanel {

    public DashboardView(NavigationManager nav, Grafo grafoCompleto, UsuarioController userController) {
        // Obtenemos al usuario que acaba de iniciar sesión
        Usuario usuarioLogueado = nav.getUsuarioLogueado();

        // --- LÓGICA DE FILTRADO PARA LA VISTA ---
        // Se visualiza solo el círculo cercano del usuario logueado
        Grafo grafoFiltrado = filtrarGrafoParaUsuario(grafoCompleto, usuarioLogueado);

        SwingUtilities.invokeLater(() -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame frame) {
                frame.setSize(1500, 750);
                frame.setLocationRelativeTo(null);
            }
        });

        setLayout(new BorderLayout());

        // 2. CABECERA
        String saludo = (usuarioLogueado != null) ? "Bienvenido, " + usuarioLogueado.getNombreCompleto() : "Red Social";
        JLabel titulo = new JLabel(saludo + " - Mis Amistades Directas", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(245, 245, 245));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // 3. CENTRO: GrafoView usando el grafo FILTRADO
        GrafoView grafoView = new GrafoView(grafoFiltrado, nav, false);
        JScrollPane scrollPane = new JScrollPane(grafoView);

        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        // 4. LATERAL: Menú de Acciones
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(200, 0));
        sidePanel.setBackground(new Color(235, 235, 235));
        sidePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JButton btnControlAmistades = new JButton("Control de amistades");
        JButton btnVerDetalle = new JButton("Ver Mis Intereses");
        JButton btnLogout = new JButton("Cerrar Sesión");

        btnControlAmistades.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerDetalle.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension btnSize = new Dimension(160, 35);
        btnControlAmistades.setMaximumSize(btnSize);
        btnVerDetalle.setMaximumSize(btnSize);
        btnLogout.setMaximumSize(btnSize);

        // --- LISTENERS ---

        // IR A CONTROL DE AMISTADES (Nueva Vista)
        btnControlAmistades.addActionListener(e -> {
            nav.goToAmistades(); // Navega a la vista donde se ven todos los nodos
        });

        btnVerDetalle.addActionListener(e -> {
            if (usuarioLogueado != null) {
                nav.goToPerfil(usuarioLogueado);
            }
        });

        btnLogout.addActionListener(e -> nav.goToLogin());

        JLabel lblMenu = new JLabel("ACCIONES");
        lblMenu.setFont(new Font("Arial", Font.BOLD, 12));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(lblMenu);
        sidePanel.add(Box.createVerticalStrut(25));
        sidePanel.add(btnControlAmistades);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(btnVerDetalle);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(btnLogout);

        add(titulo, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
    }

    /**
     * Filtra el grafo para mostrar únicamente la vecindad inmediata del usuario.
     */
    private Grafo filtrarGrafoParaUsuario(Grafo original, Usuario usuario) {
        Grafo filtrado = new Grafo();
        if (usuario == null) return filtrado;

        filtrado.agregarUsuario(usuario);
        List<Usuario> amigos = original.getAmigos(usuario);

        for (Usuario amigo : amigos) {
            filtrado.agregarUsuario(amigo);
            filtrado.agregarAmistad(usuario, amigo);
        }
        return filtrado;
    }
}