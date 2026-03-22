package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/nombre_tu_bd?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "tu_password";

    private static Connection conexion = null;

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Cargar el driver (Opcional en versiones nuevas de JDK, pero recomendado)
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✅ Conexión exitosa a MySQL");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}