package cl.biblioteca.datos;

import cl.biblioteca.modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    // LISTAR TODOS LOS LIBROS
    public List<Libro> listar() throws SQLException {

        List<Libro> libros = new ArrayList<>();

        String sql = """
                SELECT id, titulo, autor, isbn,
                       editorial, stock, id_categoria
                FROM libros
                ORDER BY id
                """;

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("editorial"),
                        rs.getInt("stock"),
                        rs.getInt("id_categoria")
                );

                libros.add(libro);
            }
        }

        return libros;
    }

    // CREAR UN NUEVO LIBRO
    public void crear(Libro libro) throws SQLException {

        String sql = """
                INSERT INTO libros
                (titulo, autor, isbn, editorial, stock, id_categoria)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());

            ps.executeUpdate();
        }
    }
    // ACTUALIZAR UN LIBRO
    public boolean actualizar(Libro libro) throws SQLException {

        String sql = """
            UPDATE libros
            SET titulo = ?, autor = ?, isbn = ?,
                editorial = ?, stock = ?, id_categoria = ?
            WHERE id = ?
            """;

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.setInt(7, libro.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR UN LIBRO
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conexion =
                     ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}