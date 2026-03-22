package org.example.view;

import org.example.controller.AuthController;
import org.example.model.Grafo;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {

    public LoginView(NavigationManager nav, AuthController controller, Grafo grafo) {
        // Layout consistente
        setLayout(new GridLayout(7, 2, 5, 5));

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();

        JButton btnLogin = new JButton("Login");
        JButton btnRecuperar = new JButton("Recuperar Contraseña");
        JButton btnRegistro = new JButton("Registrarse");

        JLabel mensaje = new JLabel("", SwingConstants.CENTER);
        mensaje.setForeground(Color.RED);

        // Añadir componentes
        add(new JLabel("Usuario:"));
        add(txtUser);
        add(new JLabel("Contraseña:"));
        add(txtPass);
        add(btnLogin);
        add(btnRecuperar);
        add(btnRegistro);
        add(new JLabel()); // Espacio vacío
        add(new JLabel("Estado:"));
        add(mensaje);

        // ACCIÓN: LOGIN
        btnLogin.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                mensaje.setText("❌ Campos vacíos");
                return;
            }

            try {
                Usuario u = controller.login(user, pass);
                JOptionPane.showMessageDialog(this, "✅ Bienvenido " + u.getNombreCompleto());
                nav.goToDashboard(); // Navegación centralizada
            } catch (Exception ex) {
                mensaje.setText("❌ " + ex.getMessage());
            }
        });

        // NAVEGACIÓN
        btnRecuperar.addActionListener(e -> nav.goToRecuperar());
        btnRegistro.addActionListener(e -> nav.goToRegistro());
    }
}