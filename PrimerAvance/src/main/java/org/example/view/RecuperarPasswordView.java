package org.example.view;

import org.example.controller.AuthController;
import org.example.model.Grafo;

import javax.swing.*;
import java.awt.*;

public class RecuperarPasswordView extends JPanel {

    public RecuperarPasswordView(NavigationManager nav, AuthController controller, Grafo grafo) {
        setLayout(new GridLayout(6, 2, 5, 5));

        JTextField txtUser = new JTextField();
        JPasswordField txtNuevaPass = new JPasswordField();

        JButton btnCambiar = new JButton("Cambiar Contraseña");
        JButton btnVolver = new JButton("Volver a Login");
        JButton btnRegistro = new JButton("Registrarse");

        JLabel mensaje = new JLabel("", SwingConstants.CENTER);

        add(new JLabel("Usuario:"));
        add(txtUser);
        add(new JLabel("Nueva Contraseña:"));
        add(txtNuevaPass);
        add(btnCambiar);
        add(btnVolver);
        add(btnRegistro);
        add(new JLabel());
        add(new JLabel("Estado:"));
        add(mensaje);

        // ACCIÓN: CAMBIAR PASSWORD
        btnCambiar.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtNuevaPass.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                mensaje.setText("❌ Rellene los campos");
                return;
            }

            try {
                controller.recuperarPassword(user, pass);
                mensaje.setText("✅ Contraseña actualizada");
                txtNuevaPass.setText(""); // Limpiar por seguridad
            } catch (Exception ex) {
                mensaje.setText("❌ " + ex.getMessage());
            }
        });

        // NAVEGACIÓN
        btnVolver.addActionListener(e -> nav.goToLogin());
        btnRegistro.addActionListener(e -> nav.goToRegistro());
    }
}