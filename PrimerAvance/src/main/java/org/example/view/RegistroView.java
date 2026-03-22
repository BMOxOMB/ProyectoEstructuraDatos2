package org.example.view;

import org.example.controller.UsuarioController;
import org.example.controller.AuthController;
import org.example.model.Grafo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class RegistroView extends JPanel {

    private AvatarView avatarPreview;

    public RegistroView(NavigationManager nav, UsuarioController usuarioController, AuthController authController, Grafo grafo) {
        // GridLayout de 12 filas para acomodar todos los campos
        setLayout(new GridLayout(12, 2, 5, 5));

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellido1 = new JTextField();
        JTextField txtApellido2 = new JTextField();

        // 📅 Configuración del Spinner para fecha
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        // Configuración de Avatar
        JComboBox<String> comboAvatar = new JComboBox<>(new String[]{"default.png", "masculino.png", "femenino.png"});
        avatarPreview = new AvatarView();
        actualizarPreview("default.png");

        JButton btnCrear = new JButton("Crear Usuario");
        JButton btnLogin = new JButton("Ir a Login");
        JButton btnRecuperar = new JButton("Recuperar Contraseña");
        JLabel mensaje = new JLabel("", SwingConstants.CENTER);

        // Añadir componentes al layout
        add(new JLabel("Usuario:")); add(txtUser);
        add(new JLabel("Contraseña:")); add(txtPass);
        add(new JLabel("Nombre:")); add(txtNombre);
        add(new JLabel("1er Apellido:")); add(txtApellido1);
        add(new JLabel("2do Apellido:")); add(txtApellido2);
        add(new JLabel("F. Nacimiento:")); add(dateSpinner);
        add(new JLabel("Seleccionar Avatar:")); add(comboAvatar);
        add(new JLabel("Vista Previa:")); add(avatarPreview);
        add(btnCrear); add(btnLogin);
        add(btnRecuperar); add(new JLabel());
        add(new JLabel("Estado:")); add(mensaje);

        // EVENTO: Cambio de imagen en combo
        comboAvatar.addActionListener(e -> actualizarPreview((String) comboAvatar.getSelectedItem()));

        // ACCIÓN: CREAR USUARIO
        btnCrear.addActionListener(e -> {
            try {
                // Validación básica
                if(txtUser.getText().isEmpty() || txtNombre.getText().isEmpty()) {
                    mensaje.setText("❌ Datos incompletos");
                    return;
                }

                Date fechaSeleccionada = (Date) dateSpinner.getValue();
                LocalDate fecha = fechaSeleccionada.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                usuarioController.crearUsuario(
                        txtUser.getText(),
                        new String(txtPass.getPassword()),
                        txtNombre.getText(),
                        txtApellido1.getText(),
                        txtApellido2.getText(),
                        fecha,
                        (String) comboAvatar.getSelectedItem()
                );

                JOptionPane.showMessageDialog(this, "✅ Usuario registrado con éxito");
                nav.goToLogin(); // Redirigir automáticamente

            } catch (Exception ex) {
                mensaje.setText("❌ " + ex.getMessage());
            }
        });

        // NAVEGACIÓN
        btnLogin.addActionListener(e -> nav.goToLogin());
        btnRecuperar.addActionListener(e -> nav.goToRecuperar());
    }

    private void actualizarPreview(String nombreImagen) {
        try {
            var url = getClass().getResource("/img/" + nombreImagen);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                avatarPreview.setImage(icon.getImage());
                avatarPreview.repaint(); // Forzar redibujado de la imagen
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + nombreImagen);
        }
    }
}