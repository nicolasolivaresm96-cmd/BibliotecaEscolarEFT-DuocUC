package cl.biblioteca.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static ConexionBD instancia;

    private static final String URL =
            "jdbc:mysql://localhost:3306/biblioteca";

    private static final String USUARIO = "root";

    private ConexionBD() {
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection obtenerConexion() throws SQLException {
        String password = System.getenv("BIBLIOTECA_DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new SQLException(
                    "Falta configurar BIBLIOTECA_DB_PASSWORD"
            );
        }

        return DriverManager.getConnection(URL, USUARIO, password);
    }
}