package org.example.DAO;

import org.example.database.ConexionDB;
import org.example.model.Grafo;
import org.example.model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {

    // Requisito: Guardar Usuario en DB
    public void guardar(Usuario u) {
        String sql = "INSERT INTO usuarios (username, password, nombre, apellido1, apellido2, fecha_nacimiento, avatar) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, "password_encriptada_o_plana"); // Aquí podrías aplicar criptografía si lo deseas
            ps.setString(3, u.getNombreCompleto().split(" ")[0]); // Simplificado para el ejemplo
            ps.setString(4, "Apellido1");
            ps.setString(5, "Apellido2");
            ps.setDate(6, Date.valueOf(u.getFechaNacimiento()));
            ps.setString(7, u.getAvatar());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar: " + e.getMessage());
            // Lanzamos excepción para que el Controller la capture y la vista muestre el error
            throw new RuntimeException("No se pudo guardar el usuario en la base de datos.");
        }
    }

    public void cargarAmistadesEnGrafo(Grafo grafo, Map<String, Usuario> mapaUsuarios) {
        String sql = "SELECT usuario_origen, usuario_destino FROM amistades";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u1 = mapaUsuarios.get(rs.getString("usuario_origen"));
                Usuario u2 = mapaUsuarios.get(rs.getString("usuario_destino"));
                if (u1 != null && u2 != null) {
                    grafo.agregarAmistad(u1, u2);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Requisito: Buscar por Username (para Login)
    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Mapeo de Tabla -> Objeto Usuario
                return new Usuario(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getDate("fecha_nacimiento").toLocalDate(),
                        rs.getString("avatar")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Requisito: Obtener todos para cargar el Grafo al iniciar
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getDate("fecha_nacimiento").toLocalDate(),
                        rs.getString("avatar")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}