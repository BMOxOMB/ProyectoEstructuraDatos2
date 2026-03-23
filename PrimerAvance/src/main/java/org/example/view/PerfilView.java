package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PerfilView extends JPanel {
    private DefaultListModel<String> listModel;
    private JList<String> listaUI;

    public PerfilView(NavigationManager nav, Usuario usuario, UsuarioController userController) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setBackground(Color.WHITE);

        // --- ENCABEZADO ---
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        JLabel nameLabel = new JLabel(usuario.getNombreCompleto(), JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(nameLabel);
        add(header, BorderLayout.NORTH);

        // --- LISTA DE MIS INTERESES ---
        listModel = new DefaultListModel<>();
        usuario.getIntereses().forEach(listModel::addElement);
        listaUI = new JList<>(listModel);
        add(new JScrollPane(listaUI), BorderLayout.CENTER);

        // --- PANEL DE ACCIONES ---
        JPanel actions = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Agregar Interés del Catálogo");
        JButton btnVolver = new JButton("Volver");

        btnAdd.addActionListener(e -> {
            // Obtenemos los intereses reales de la tabla catalogo_intereses
            List<String> catalogo = userController.getCatalogoCompleto();
            String[] opciones = catalogo.toArray(new String[0]);

            String seleccion = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecciona un interés del catálogo oficial:",
                    "Catálogo de Intereses",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion != null && !listModel.contains(seleccion)) {
                userController.agregarInteres(usuario, seleccion);
                listModel.addElement(seleccion);
            }
        });

        btnVolver.addActionListener(e -> nav.goToDashboard());

        actions.add(btnAdd);
        actions.add(btnVolver);
        add(actions, BorderLayout.SOUTH);
    }
}