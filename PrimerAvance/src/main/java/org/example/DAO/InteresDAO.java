package org.example.DAO;

import org.example.database.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InteresDAO {
    public InteresDAO() {
        // Constructor vacío
    }

    // Obtiene todos los intereses disponibles en el sistema (Catalogo)
    public List<String> obtenerTodosLosIntereses() {
        List<String> intereses = new ArrayList<>();
        String sql = "SELECT nombre_interes FROM catalogo_intereses ORDER BY nombre_interes ASC";

        // Obtenemos la conexión directamente de tu clase ConexionDB dentro del try
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
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

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
        // 1. Validar que los datos no lleguen vacíos
        if (username == null || nombreInteres == null) {
            System.out.println("⚠️ Error: Username o Interés nulo en el DAO");
            return;
        }

        String sql = "INSERT INTO usuario_intereses (username, interes_id) " +
                "VALUES (?, (SELECT id FROM catalogo_intereses WHERE nombre_interes = ?))";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, nombreInteres);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ DB: Guardado exitoso -> " + nombreInteres);
            }

        } catch (SQLException e) {
            // ERROR 1062: Entrada duplicada (El usuario ya tiene ese interés)
            if (e.getErrorCode() == 1062) {
                System.out.println("El usuario '" + username + "' ya tenía el interés '" + nombreInteres + "'.");
            } else {
                System.err.println("❌ ERROR SQL REAL: " + e.getMessage());
                e.printStackTrace(); // Esto te dirá la línea exacta del error
            }
        }
    }

    // Elimina la relación de la base de datos
    public void eliminarInteresUsuario(String username, String nombreInteres) {
        String sql = "DELETE FROM usuario_intereses WHERE username = ? AND " +
                "interes_id = (SELECT id FROM catalogo_intereses WHERE nombre_interes = ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, nombreInteres);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}