package cl.biblioteca.datos;

import cl.biblioteca.modelo.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    // LISTAR ESTUDIANTES
    public List<Estudiante> listar() throws SQLException {

        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = "SELECT * FROM estudiantes ORDER BY id";

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Estudiante estudiante = new Estudiante(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rut"),
                        rs.getString("curso"),
                        rs.getString("correo")
                );

                estudiantes.add(estudiante);
            }
        }

        return estudiantes;
    }

    // CREAR ESTUDIANTE
    public void crear(Estudiante estudiante) throws SQLException {

        String sql = """
                INSERT INTO estudiantes
                (nombre, rut, curso, correo)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());

            ps.executeUpdate();
        }
    }

    // ACTUALIZAR ESTUDIANTE
    public boolean actualizar(Estudiante estudiante)
            throws SQLException {

        String sql = """
                UPDATE estudiantes
                SET nombre = ?, rut = ?, curso = ?, correo = ?
                WHERE id = ?
                """;

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.setInt(5, estudiante.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR ESTUDIANTE
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}