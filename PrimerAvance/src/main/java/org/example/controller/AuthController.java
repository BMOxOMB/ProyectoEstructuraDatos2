package org.example.controller;

import org.example.model.Usuario;
import org.example.service.AuthService;

public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    public Usuario login(String user, String pass) {
        // Validación previa (Control de errores según rúbrica)
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el nombre de usuario.");
        }
        if (pass == null || pass.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la contraseña.");
        }

        // El servicio se encarga de buscar al usuario y validar la clave
        return service.login(user, pass);
    }

    public void recuperarPassword(String user, String nuevaPass) {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("Especifique el usuario para recuperar.");
        }
        if (nuevaPass == null || nuevaPass.length() < 4) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 4 caracteres.");
        }

        service.recuperarPassword(user, nuevaPass);
    }
}