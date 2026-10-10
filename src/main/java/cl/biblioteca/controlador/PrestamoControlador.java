package cl.biblioteca.controlador;

import cl.biblioteca.datos.EstudianteDAO;
import cl.biblioteca.datos.LibroDAO;
import cl.biblioteca.datos.PrestamoDAO;

import cl.biblioteca.modelo.Estudiante;
import cl.biblioteca.modelo.Libro;
import cl.biblioteca.modelo.Prestamo;

import cl.biblioteca.vista.VentanaPrestamos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PrestamoControlador {

    private final VentanaPrestamos vista;

    private final PrestamoDAO prestamoDAO;
    private final EstudianteDAO estudianteDAO;
    private final LibroDAO libroDAO;

    private boolean operacionEnCurso = false;

    public PrestamoControlador(VentanaPrestamos vista) {

        this.vista = vista;

        this.prestamoDAO = new PrestamoDAO();
        this.estudianteDAO = new EstudianteDAO();
        this.libroDAO = new LibroDAO();

        vista.getBtnRegistrar().addActionListener(
                e -> registrarPrestamo()
        );

        vista.getBtnDevolver().addActionListener(
                e -> registrarDevolucion()
        );

        vista.getBtnActualizar().addActionListener(
                e -> cargarPrestamos()
        );

        cargarPrestamos();
    }

    // ========================================
    // MOSTRAR PRÉSTAMOS
    // ========================================

    public void cargarPrestamos() {

        if (operacionEnCurso) {
            return;
        }

        try {

            List<Prestamo> prestamos = prestamoDAO.listar();

            DefaultTableModel modelo = vista.getModeloTabla();

            modelo.setRowCount(0);

            for (Prestamo prestamo : prestamos) {

                String fechaDevolucion =
                        prestamo.getFechaDevolucion() == null
                                ? "Pendiente"
                                : prestamo.getFechaDevolucion().toString();

                String estado = prestamo.isDevuelto()
                        ? "Devuelto"
                        : "Pendiente";

                modelo.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getIdEstudiante(),
                        prestamo.getIdLibro(),
                        prestamo.getFechaPrestamo(),
                        fechaDevolucion,
                        estado
                });
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al cargar préstamos: " + e.getMessage()
            );
        }
    }

    // ========================================
    // REGISTRAR PRÉSTAMO
    // ========================================

    private void registrarPrestamo() {

        if (operacionEnCurso) {
            return;
        }

        try {

            List<Estudiante> estudiantes = estudianteDAO.listar();
            List<Libro> libros = libroDAO.listar();

            if (estudiantes.isEmpty()) {

                JOptionPane.showMessageDialog(
                        vista,
                        "No existen estudiantes registrados."
                );

                return;
            }

            DefaultComboBoxModel<Opcion> modeloEstudiantes =
                    new DefaultComboBoxModel<>();

            for (Estudiante estudiante : estudiantes) {

                modeloEstudiantes.addElement(
                        new Opcion(
                                estudiante.getId(),
                                estudiante.getNombre()
                                        + " - "
                                        + estudiante.getRut()
                        )
                );
            }

            DefaultComboBoxModel<Opcion> modeloLibros =
                    new DefaultComboBoxModel<>();

            for (Libro libro : libros) {

                if (libro.getStock() > 0) {

                    modeloLibros.addElement(
                            new Opcion(
                                    libro.getId(),
                                    libro.getTitulo()
                                            + " (Stock: "
                                            + libro.getStock()
                                            + ")"
                            )
                    );
                }
            }

            if (modeloLibros.getSize() == 0) {

                JOptionPane.showMessageDialog(
                        vista,
                        "No hay libros con stock disponible."
                );

                return;
            }

            JComboBox<Opcion> cmbEstudiante =
                    new JComboBox<>(modeloEstudiantes);

            JComboBox<Opcion> cmbLibro =
                    new JComboBox<>(modeloLibros);

            Object[] campos = {
                    "Estudiante:", cmbEstudiante,
                    "Libro disponible:", cmbLibro
            };

            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    campos,
                    "Registrar nuevo préstamo",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            Opcion estudianteSeleccionado =
                    (Opcion) cmbEstudiante.getSelectedItem();

            Opcion libroSeleccionado =
                    (Opcion) cmbLibro.getSelectedItem();

            if (estudianteSeleccionado == null
                    || libroSeleccionado == null) {

                mostrarError(
                        "Debes seleccionar un estudiante y un libro."
                );

                return;
            }

            int idEstudiante = estudianteSeleccionado.id;
            int idLibro = libroSeleccionado.id;

            // El registro se ejecuta fuera del hilo de Swing.
            SwingWorker<Boolean, Void> trabajador =
                    new SwingWorker<>() {

                        @Override
                        protected Boolean doInBackground()
                                throws Exception {

                            return prestamoDAO.registrar(
                                    idEstudiante,
                                    idLibro
                            );
                        }

                        @Override
                        protected void done() {

                            finalizarOperacion();

                            try {

                                boolean registrado = get();

                                if (registrado) {

                                    JOptionPane.showMessageDialog(
                                            vista,
                                            "Préstamo registrado correctamente."
                                    );

                                } else {

                                    JOptionPane.showMessageDialog(
                                            vista,
                                            "No se pudo registrar el préstamo.\n"
                                                    + "Es posible que el libro ya no tenga stock."
                                    );
                                }

                                cargarPrestamos();

                            } catch (InterruptedException ex) {

                                Thread.currentThread().interrupt();

                                mostrarError(
                                        "La operación fue interrumpida."
                                );

                            } catch (ExecutionException ex) {

                                mostrarError(
                                        "Error al registrar préstamo: "
                                                + mensajeCausa(ex)
                                );
                            }
                        }
                    };

            iniciarOperacion();
            trabajador.execute();

        } catch (SQLException e) {

            mostrarError(
                    "Error al consultar estudiantes o libros: "
                            + e.getMessage()
            );
        }
    }

    // ========================================
    // REGISTRAR DEVOLUCIÓN
    // ========================================

    private void registrarDevolucion() {

        if (operacionEnCurso) {
            return;
        }

        int fila = vista.getTablaPrestamos().getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Selecciona un préstamo para registrar su devolución."
            );

            return;
        }

        int filaModelo = vista.getTablaPrestamos()
                .convertRowIndexToModel(fila);

        DefaultTableModel modelo = vista.getModeloTabla();

        int idPrestamo = (int) modelo.getValueAt(
                filaModelo,
                0
        );

        String estado = modelo.getValueAt(
                filaModelo,
                5
        ).toString();

        if ("Devuelto".equals(estado)) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Este préstamo ya fue devuelto."
            );

            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Confirmas la devolución del préstamo N.º "
                        + idPrestamo + "?",
                "Registrar devolución",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        SwingWorker<Boolean, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected Boolean doInBackground()
                            throws Exception {

                        return prestamoDAO.devolver(
                                idPrestamo
                        );
                    }

                    @Override
                    protected void done() {

                        finalizarOperacion();

                        try {

                            boolean devuelto = get();

                            if (devuelto) {

                                JOptionPane.showMessageDialog(
                                        vista,
                                        "Devolución registrada correctamente.\n"
                                                + "El stock del libro fue actualizado."
                                );

                            } else {

                                JOptionPane.showMessageDialog(
                                        vista,
                                        "No se pudo registrar la devolución.\n"
                                                + "El préstamo podría estar devuelto."
                                );
                            }

                            cargarPrestamos();

                        } catch (InterruptedException ex) {

                            Thread.currentThread().interrupt();

                            mostrarError(
                                    "La operación fue interrumpida."
                            );

                        } catch (ExecutionException ex) {

                            mostrarError(
                                    "Error al registrar devolución: "
                                            + mensajeCausa(ex)
                            );
                        }
                    }
                };

        iniciarOperacion();
        trabajador.execute();
    }

    // ========================================
    // CONTROL DE OPERACIONES
    // ========================================

    private void iniciarOperacion() {

        operacionEnCurso = true;

        vista.getBtnRegistrar().setEnabled(false);
        vista.getBtnDevolver().setEnabled(false);
        vista.getBtnActualizar().setEnabled(false);
    }

    private void finalizarOperacion() {

        operacionEnCurso = false;

        vista.getBtnRegistrar().setEnabled(true);
        vista.getBtnDevolver().setEnabled(true);
        vista.getBtnActualizar().setEnabled(true);
    }

    // ========================================
    // OPCIONES DE LOS COMBOBOX
    // ========================================

    private static class Opcion {

        private final int id;
        private final String descripcion;

        public Opcion(int id, String descripcion) {

            this.id = id;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    // ========================================
    // MANEJO DE ERRORES
    // ========================================

    private String mensajeCausa(ExecutionException ex) {

        Throwable causa = ex.getCause();

        return causa != null
                ? causa.getMessage()
                : ex.getMessage();
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                vista,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}