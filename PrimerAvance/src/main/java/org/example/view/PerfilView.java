package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PerfilView extends JPanel {
    private DefaultListModel<String> listModel;
    private JList<String> listaUI;
    private DefaultListModel<String> modelSugerencias; // Movido a atributo para refrescarlo

    public PerfilView(NavigationManager nav, Usuario usuario, UsuarioController userController) {
        // --- RECOMENDACIÓN CLAVE: Cargar datos reales de la DB antes de iniciar ---
        userController.cargarInteresesDelUsuario(usuario);

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setBackground(Color.WHITE);

        // --- ENCABEZADO ---
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        JLabel nameLabel = new JLabel(usuario.getNombreCompleto(), JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel usernameLabel = new JLabel("@" + usuario.getUsername(), JLabel.CENTER);
        header.add(nameLabel);
        header.add(usernameLabel);
        add(header, BorderLayout.NORTH);

        // --- PANEL CENTRAL: MIS INTERESES ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Mis Intereses Actuales"));

        listModel = new DefaultListModel<>();
        // Ahora usuario.getIntereses() ya tiene los datos de la DB gracias al cargarInteresesDelUsuario
        if (usuario.getIntereses() != null) {
            usuario.getIntereses().forEach(listModel::addElement);
        }

        listaUI = new JList<>(listModel);
        centerPanel.add(new JScrollPane(listaUI), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- PANEL DERECHO: SUGERENCIAS (BFS) ---
        modelSugerencias = new DefaultListModel<>();
        actualizarSugerencias(usuario, userController); // Método auxiliar creado abajo

        JList<String> listaSugerencias = new JList<>(modelSugerencias);
        JPanel pnlSugerencias = new JPanel(new BorderLayout());
        pnlSugerencias.setPreferredSize(new Dimension(250, 0));
        pnlSugerencias.setBorder(BorderFactory.createTitledBorder("Gente con tus gustos"));
        pnlSugerencias.add(new JScrollPane(listaSugerencias), BorderLayout.CENTER);
        add(pnlSugerencias, BorderLayout.EAST);

        // --- PANEL DE ACCIONES (SUR) ---
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actions.setOpaque(false);

        JButton btnAdd = new JButton("Agregar Interés");
        JButton btnDelete = new JButton("Eliminar Interés");
        JButton btnVolver = new JButton("Volver al Dashboard");

        // Lógica: Agregar
        btnAdd.addActionListener(e -> {
            List<String> catalogo = userController.getCatalogoCompleto();
            if (catalogo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El catálogo de la DB está vacío. Revisa la tabla catalogo_intereses.");
                return;
            }
            String[] opciones = catalogo.toArray(new String[0]);
            String seleccion = (String) JOptionPane.showInputDialog(
                    this, "Selecciona un interés:", "Catálogo",
                    JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            if (seleccion != null && !listModel.contains(seleccion)) {
                userController.agregarInteres(usuario, seleccion);
                listModel.addElement(seleccion);

                // Al agregar un interés, las sugerencias BFS cambian, las refrescamos:
                actualizarSugerencias(usuario, userController);
            }
        });

        // Lógica: Eliminar
        btnDelete.addActionListener(e -> {
            String seleccionado = listaUI.getSelectedValue();
            if (seleccionado != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Seguro que quieres eliminar '" + seleccionado + "'?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    userController.eliminarInteres(usuario, seleccionado);
                    listModel.removeElement(seleccionado);

                    // Al eliminar, también refrescamos sugerencias
                    actualizarSugerencias(usuario, userController);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un interés de la lista para borrarlo.");
            }
        });

        btnVolver.addActionListener(e -> nav.goToDashboard());

        actions.add(btnAdd);
        actions.add(btnDelete);
        actions.add(btnVolver);
        add(actions, BorderLayout.SOUTH);
    }

    /**
     * Método auxiliar para refrescar la lista de sugerencias (BFS)
     */
    private void actualizarSugerencias(Usuario usuario, UsuarioController userController) {
        modelSugerencias.clear();
        List<Usuario> sugeridos = userController.obtenerSugerenciasAmistad(usuario);
        if (sugeridos.isEmpty()) {
            modelSugerencias.addElement("Sin sugerencias nuevas");
        } else {
            for (Usuario u : sugeridos) {
                modelSugerencias.addElement(u.getNombreCompleto() + " (@" + u.getUsername() + ")");
            }
        }
    }
}