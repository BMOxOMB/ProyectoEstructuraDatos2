package org.example.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InteresDAO {
    private Connection connection; // Asumiendo que recibes la conexión

    public InteresDAO(Connection connection) {
        this.connection = connection;
    }

    // Obtiene todos los intereses disponibles en el sistema (Catalogo)
    public List<String> obtenerTodosLosIntereses() {
        List<String> intereses = new ArrayList<>();
        String sql = "SELECT nombre_interes FROM catalogo_intereses ORDER BY nombre_interes ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                intereses.add(rs.getString("nombre_interes"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return intereses;
    }

    // Obtiene los intereses específicos de un usuario
    public List<String> obtenerInteresesUsuario(String username) {
        List<String> intereses = new ArrayList<>();
        String sql = "SELECT c.nombre_interes FROM catalogo_intereses c " +
                "JOIN usuario_intereses ui ON c.id = ui.interes_id " +
                "WHERE ui.username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    intereses.add(rs.getString("nombre_interes"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return intereses;
    }


    // Guarda la relación en la base de datos
    public void guardarInteresUsuario(String username, String nombreInteres) {
        String sql = "INSERT INTO usuario_intereses (username, interes_id) " +
                "VALUES (?, (SELECT id FROM catalogo_intereses WHERE nombre_interes = ?))";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, nombreInteres);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("El usuario ya tiene ese interés.");
        }
    }

    public void eliminarInteresUsuario(String username, String nombreInteres) {
        String sql = "DELETE FROM usuario_intereses WHERE username = ? AND " +
                "interes_id = (SELECT id FROM catalogo_intereses WHERE nombre_interes = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, nombreInteres);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}