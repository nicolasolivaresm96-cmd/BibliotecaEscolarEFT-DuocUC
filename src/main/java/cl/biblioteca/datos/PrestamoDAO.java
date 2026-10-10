package cl.biblioteca.datos;

import cl.biblioteca.modelo.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    // LISTAR TODOS LOS PRÉSTAMOS
    public List<Prestamo> listar() throws SQLException {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
                SELECT id, id_estudiante, id_libro,
                       fecha_prestamo, fecha_devolucion, devuelto
                FROM prestamos
                ORDER BY id
                """;

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Date fechaDevolucion = rs.getDate("fecha_devolucion");

                Prestamo prestamo = new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        fechaDevolucion != null
                                ? fechaDevolucion.toLocalDate()
                                : null,
                        rs.getBoolean("devuelto")
                );

                prestamos.add(prestamo);
            }
        }

        return prestamos;
    }
    // REGISTRAR PRÉSTAMO Y DESCONTAR STOCK
    public synchronized boolean registrar(int idEstudiante, int idLibro)
            throws SQLException {

        String sqlStock = """
            UPDATE libros
            SET stock = stock - 1
            WHERE id = ? AND stock > 0
            """;

        String sqlPrestamo = """
            INSERT INTO prestamos
            (id_estudiante, id_libro, fecha_prestamo,
             fecha_devolucion, devuelto)
            VALUES (?, ?, CURDATE(), NULL, FALSE)
            """;

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                // Descontar stock solo si hay unidades disponibles
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlStock)) {

                    ps.setInt(1, idLibro);

                    if (ps.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                // Registrar el préstamo
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlPrestamo)) {

                    ps.setInt(1, idEstudiante);
                    ps.setInt(2, idLibro);
                    ps.executeUpdate();
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }
    // DEVOLVER LIBRO Y RESTAURAR STOCK
    public synchronized boolean devolver(int idPrestamo)
            throws SQLException {

        String sqlBuscar = """
            SELECT id_libro, devuelto
            FROM prestamos
            WHERE id = ?
            FOR UPDATE
            """;

        String sqlDevolver = """
            UPDATE prestamos
            SET devuelto = TRUE,
                fecha_devolucion = CURDATE()
            WHERE id = ?
            """;

        String sqlStock = """
            UPDATE libros
            SET stock = stock + 1
            WHERE id = ?
            """;

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                int idLibro;

                // 1. Buscar y bloquear el préstamo
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlBuscar)) {

                    ps.setInt(1, idPrestamo);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next() || rs.getBoolean("devuelto")) {
                            conexion.rollback();
                            return false;
                        }

                        idLibro = rs.getInt("id_libro");
                    }
                }

                // 2. Marcar préstamo como devuelto
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlDevolver)) {

                    ps.setInt(1, idPrestamo);
                    ps.executeUpdate();
                }

                // 3. Restaurar una unidad de stock
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlStock)) {

                    ps.setInt(1, idLibro);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo restaurar el stock."
                        );
                    }
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }
}