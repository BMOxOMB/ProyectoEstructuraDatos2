package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Grafo;
import org.example.model.Usuario;
import javax.swing.*;
import java.awt.*;

public class AmistadesView extends JPanel {

    public AmistadesView(NavigationManager nav, Grafo grafo, UsuarioController userController) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- 1. PANEL SUPERIOR: Instrucciones ---
        JPanel pnlInstrucciones = new JPanel(new GridLayout(2, 1));
        pnlInstrucciones.setBackground(new Color(60, 63, 65));

        JLabel lblInst1 = new JLabel("🖱️ Click Izquierdo sobre un usuario: AGREGAR a mis amigos", JLabel.CENTER);
        JLabel lblInst2 = new JLabel("🖱️ Click Derecho sobre un usuario: ELIMINAR de mis amigos", JLabel.CENTER);

        lblInst1.setForeground(Color.WHITE);
        lblInst2.setForeground(Color.CYAN);
        pnlInstrucciones.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        pnlInstrucciones.add(lblInst1);
        pnlInstrucciones.add(lblInst2);
        add(pnlInstrucciones, BorderLayout.NORTH);

        // --- 2. PANEL CENTRAL: El Grafo Interactivo ---
        GrafoView grafoView = new GrafoView(grafo, nav, true);
        JScrollPane scrollPane = new JScrollPane(grafoView);

        // --- FIX PARA EL EFECTO FANTASMA (Igual al de DashboardView) ---
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // --- 3. PANEL INFERIOR: Botón de Salida ---
        JPanel pnlInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlInferior.setBackground(Color.WHITE);
        pnlInferior.setOpaque(true);

        pnlInferior.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnVolver = new JButton("Finalizar y Volver al Dashboard");
        btnVolver.setPreferredSize(new Dimension(300, 40));
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> nav.goToDashboard());

        pnlInferior.add(btnVolver);
        add(pnlInferior, BorderLayout.SOUTH);
    }
}