package org.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Usuario {

    private String username;
    private String password;
    private String primerNombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;
    private String avatar;

    private String nombreGrupo;
    private String colorGrupo;

    private List<String> intereses;

    public Usuario(String username, String password, String primerNombre,
                   String primerApellido, String segundoApellido,
                   LocalDate fechaNacimiento, String avatar) {

        this.username = username;
        this.password = password;
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = fechaNacimiento;
        this.avatar = (avatar == null || avatar.isEmpty()) ? "default.png" : avatar;
        this.intereses = new ArrayList<>();
        this.colorGrupo = "#FFFFFF";
        this.nombreGrupo = "Ninguno";
    }

    // --- MÉTODOS DE ACCESO (GETTERS) ---

    public String getUsername() { return username; }

    // Agregado para el Controller y el DAO (MySQL)
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getPrimerNombre() { return primerNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }

    public String getAvatar() { return avatar; }
    public String getColorGrupo() { return colorGrupo; }
    public String getNombreGrupo() { return nombreGrupo; }

    public String getNombreCompleto() {
        return primerNombre + " " + primerApellido + " " + segundoApellido;
    }

    // --- LÓGICA DE NEGOCIO ---

    public boolean autenticar(String passwordIngresada) {
        return this.password.equals(passwordIngresada);
    }

    public String getPassword() { return password; } // Necesario para el login en el Service

    public void cambiarPassword(String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.length() < 4) {
            throw new IllegalArgumentException("Contraseña inválida");
        }
        this.password = nuevaPassword;
    }

    public void setGrupo(String nombre, String color) {
        this.nombreGrupo = nombre;
        this.colorGrupo = color;
    }

    public List<String> getIntereses() { return intereses; }
    public void addInteres(String interes) { intereses.add(interes); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(username, usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}