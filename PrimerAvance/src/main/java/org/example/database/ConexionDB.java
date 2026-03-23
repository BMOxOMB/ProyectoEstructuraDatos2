package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/red_social_cenfotec?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "!Q02w12e22r";

    private static Connection conexion = null;

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Esto cargará la librería que tienes en el pom.xml
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (Exception e) {
            // ESTO ES LO IMPORTANTE: Mira tu consola de salida
            System.out.println("--- ERROR DE CONEXIÓN DETECTADO ---");
            e.printStackTrace();
            System.out.println("-----------------------------------");
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