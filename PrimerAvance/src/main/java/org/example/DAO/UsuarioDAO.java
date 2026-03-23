package org.example.DAO;

import org.example.database.ConexionDB;
import org.example.model.Grafo;
import org.example.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {

    public void guardar(Usuario u) {
        String sql = "INSERT INTO usuarios (username, password, nombre, apellido1, apellido2, fecha_nacimiento, avatar) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getPrimerNombre());
            ps.setString(4, u.getPrimerApellido());
            ps.setString(5, u.getSegundoApellido());
            ps.setDate(6, Date.valueOf(u.getFechaNacimiento()));
            ps.setString(7, u.getAvatar());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar usuario: " + e.getMessage());
        }
    }

    //Carga las relaciones de la tabla 'amistades' directamente al Grafo.
    public void cargarAmistadesEnGrafo(Grafo grafo) {
        // 1. Mapa temporal para relacionar Username -> Objeto Usuario
        Map<String, Usuario> mapa = new HashMap<>();
        for (Usuario u : grafo.getAdjList().keySet()) {
            mapa.put(u.getUsername(), u);
        }

        String sql = "SELECT usuario_origen, usuario_destino FROM amistades";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u1 = mapa.get(rs.getString("usuario_origen"));
                Usuario u2 = mapa.get(rs.getString("usuario_destino"));

                if (u1 != null && u2 != null) {
                    grafo.agregarAmistad(u1, u2);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar amistades: " + e.getMessage());
        }
    }

    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método privado para evitar repetir código de mapeo
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
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
}