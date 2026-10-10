package cl.biblioteca.controlador;

import cl.biblioteca.datos.LibroDAO;
import cl.biblioteca.modelo.Libro;
import cl.biblioteca.vista.VentanaLibros;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class LibroControlador {

    private final VentanaLibros vista;
    private final LibroDAO libroDAO;

    public LibroControlador(VentanaLibros vista) {

        this.vista = vista;
        this.libroDAO = new LibroDAO();

        // Conectar botones
        vista.getBtnAgregar().addActionListener(e -> agregarLibro());
        vista.getBtnEditar().addActionListener(e -> editarLibro());
        vista.getBtnEliminar().addActionListener(e -> eliminarLibro());
        vista.getBtnActualizar().addActionListener(e -> cargarLibros());

        // Cargar datos al abrir la ventana
        cargarLibros();
    }

    // ========================================
    // LISTAR LIBROS
    // ========================================

    public void cargarLibros() {

        try {
            List<Libro> libros = libroDAO.listar();
            DefaultTableModel modelo = vista.getModeloTabla();

            modelo.setRowCount(0);

            for (Libro libro : libros) {

                modelo.addRow(new Object[]{
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getIsbn(),
                        libro.getEditorial(),
                        libro.getStock(),
                        libro.getIdCategoria()
                });
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar libros: " + e.getMessage());
        }
    }

    // ========================================
    // AGREGAR LIBRO
    // ========================================

    private void agregarLibro() {

        Libro nuevo = mostrarFormulario(null);

        if (nuevo == null) {
            return;
        }

        try {
            libroDAO.crear(nuevo);

            JOptionPane.showMessageDialog(
                    vista,
                    "Libro agregado correctamente."
            );

            cargarLibros();

        } catch (SQLException e) {
            mostrarError("Error al agregar libro: " + e.getMessage());
        }
    }

    // ========================================
    // EDITAR LIBRO
    // ========================================

    private void editarLibro() {

        int fila = vista.getTablaLibros().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Selecciona un libro para editar."
            );
            return;
        }

        int filaModelo = vista.getTablaLibros()
                .convertRowIndexToModel(fila);

        DefaultTableModel modelo = vista.getModeloTabla();

        int id = (int) modelo.getValueAt(filaModelo, 0);

        Libro actual = new Libro(
                id,
                modelo.getValueAt(filaModelo, 1).toString(),
                modelo.getValueAt(filaModelo, 2).toString(),
                modelo.getValueAt(filaModelo, 3).toString(),
                modelo.getValueAt(filaModelo, 4).toString(),
                (int) modelo.getValueAt(filaModelo, 5),
                (int) modelo.getValueAt(filaModelo, 6)
        );

        Libro actualizado = mostrarFormulario(actual);

        if (actualizado == null) {
            return;
        }

        try {
            boolean resultado = libroDAO.actualizar(actualizado);

            if (resultado) {
                JOptionPane.showMessageDialog(
                        vista,
                        "Libro actualizado correctamente."
                );
                cargarLibros();
            } else {
                mostrarError("No se encontró el libro.");
            }

        } catch (SQLException e) {
            mostrarError("Error al actualizar libro: " + e.getMessage());
        }
    }

    // ========================================
    // ELIMINAR LIBRO
    // ========================================

    private void eliminarLibro() {

        int fila = vista.getTablaLibros().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Selecciona un libro para eliminar."
            );
            return;
        }

        int filaModelo = vista.getTablaLibros()
                .convertRowIndexToModel(fila);

        DefaultTableModel modelo = vista.getModeloTabla();

        int id = (int) modelo.getValueAt(filaModelo, 0);
        String titulo = modelo.getValueAt(filaModelo, 1).toString();

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Estás seguro de eliminar el libro?\n\n" + titulo,
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = libroDAO.eliminar(id);

            if (eliminado) {
                JOptionPane.showMessageDialog(
                        vista,
                        "Libro eliminado correctamente."
                );
                cargarLibros();
            } else {
                mostrarError("No se encontró el libro.");
            }

        } catch (SQLException e) {
            mostrarError(
                    "No se pudo eliminar el libro.\n" +
                            "Puede tener préstamos asociados.\n\n" +
                            e.getMessage()
            );
        }
    }

    // ========================================
    // FORMULARIO AGREGAR / EDITAR
    // ========================================

    private Libro mostrarFormulario(Libro libro) {

        boolean esEdicion = libro != null;

        JTextField txtTitulo = new JTextField(
                esEdicion ? libro.getTitulo() : ""
        );

        JTextField txtAutor = new JTextField(
                esEdicion ? libro.getAutor() : ""
        );

        JTextField txtIsbn = new JTextField(
                esEdicion ? libro.getIsbn() : ""
        );

        JTextField txtEditorial = new JTextField(
                esEdicion ? libro.getEditorial() : ""
        );

        JTextField txtStock = new JTextField(
                esEdicion ? String.valueOf(libro.getStock()) : ""
        );

        JComboBox<String> cmbCategoria = new JComboBox<>(
                new String[]{
                        "1 - Ciencia Ficción",
                        "2 - Historia",
                        "3 - Filosofía",
                        "4 - Literatura",
                        "5 - Tecnología"
                }
        );

        if (esEdicion) {
            cmbCategoria.setSelectedIndex(libro.getIdCategoria() - 1);
        }

        Object[] campos = {
                "Título:", txtTitulo,
                "Autor:", txtAutor,
                "ISBN:", txtIsbn,
                "Editorial:", txtEditorial,
                "Stock:", txtStock,
                "Categoría:", cmbCategoria
        };

        while (true) {

            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    campos,
                    esEdicion ? "Editar libro" : "Agregar libro",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }

            try {
                String titulo = txtTitulo.getText().trim();
                String autor = txtAutor.getText().trim();
                String isbn = txtIsbn.getText().trim();
                String editorial = txtEditorial.getText().trim();

                if (titulo.isEmpty() || autor.isEmpty()
                        || isbn.isEmpty() || editorial.isEmpty()
                        || txtStock.getText().trim().isEmpty()) {

                    throw new IllegalArgumentException(
                            "Todos los campos son obligatorios."
                    );
                }

                if (titulo.length() > 200
                        || autor.length() > 100
                        || isbn.length() > 20
                        || editorial.length() > 100) {

                    throw new IllegalArgumentException(
                            "Uno o más campos superan el largo permitido."
                    );
                }

                int stock = Integer.parseInt(
                        txtStock.getText().trim()
                );

                if (stock < 0) {
                    throw new IllegalArgumentException(
                            "El stock no puede ser negativo."
                    );
                }

                int categoria = cmbCategoria.getSelectedIndex() + 1;

                int id = esEdicion ? libro.getId() : 0;

                return new Libro(
                        id,
                        titulo,
                        autor,
                        isbn,
                        editorial,
                        stock,
                        categoria
                );

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        vista,
                        "El stock debe ser un número entero válido.",
                        "Error de validación",
                        JOptionPane.WARNING_MESSAGE
                );

            } catch (IllegalArgumentException e) {

                JOptionPane.showMessageDialog(
                        vista,
                        e.getMessage(),
                        "Error de validación",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    // ========================================
    // MOSTRAR ERRORES
    // ========================================

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                vista,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}