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
                // 1. Autenticar
                Usuario logueado = controller.login(user, pass);

                // 2. CARGA DINÁMICA DEL GRAFO
                // Pide al DAO todos los usuarios y amistades
                // Si no tienes acceso al DAO aquí, el NavigationManager debería tener un método 'cargarDatos'
                nav.setUsuarioLogueado(logueado); // Guardar quién entró
                nav.prepararGrafo(); // Método que vamos a crear en el Manager

                JOptionPane.showMessageDialog(this, "✅ Bienvenido " + logueado.getNombreCompleto());

                // 3. Ahora sí, ir al Dashboard con el grafo ya lleno
                nav.goToDashboard();

            } catch (Exception ex) {
                mensaje.setText("❌ " + ex.getMessage());
                ex.printStackTrace(); // Para ver errores de SQL en consola
            }
        });

        // NAVEGACIÓN
        btnRecuperar.addActionListener(e -> nav.goToRecuperar());
        btnRegistro.addActionListener(e -> nav.goToRegistro());
    }
}