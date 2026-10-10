package cl.biblioteca.controlador;

import cl.biblioteca.datos.EstudianteDAO;
import cl.biblioteca.datos.LibroDAO;
import cl.biblioteca.datos.PrestamoDAO;

import cl.biblioteca.modelo.Libro;
import cl.biblioteca.modelo.Prestamo;

import cl.biblioteca.vista.VentanaReportes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReporteControlador {

    private final VentanaReportes vista;

    private final LibroDAO libroDAO;
    private final EstudianteDAO estudianteDAO;
    private final PrestamoDAO prestamoDAO;

    public ReporteControlador(VentanaReportes vista) {

        this.vista = vista;

        this.libroDAO = new LibroDAO();
        this.estudianteDAO = new EstudianteDAO();
        this.prestamoDAO = new PrestamoDAO();

        // CONECTAR BOTÓN
        vista.getBtnActualizar().addActionListener(
                e -> cargarReportes()
        );

        // CARGAR REPORTES AL ABRIR
        cargarReportes();
    }

    // ========================================
    // CARGAR REPORTES
    // ========================================

    public void cargarReportes() {

        vista.getBtnActualizar().setEnabled(false);
        vista.getBtnActualizar().setText("Cargando...");

        SwingWorker<DatosReporte, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected DatosReporte doInBackground()
                            throws Exception {

                        List<Libro> libros = libroDAO.listar();

                        int totalEstudiantes =
                                estudianteDAO.listar().size();

                        List<Prestamo> prestamos =
                                prestamoDAO.listar();

                        int ejemplaresDisponibles = 0;

                        for (Libro libro : libros) {
                            ejemplaresDisponibles += libro.getStock();
                        }

                        int prestamosPendientes = 0;
                        int prestamosDevueltos = 0;

                        for (Prestamo prestamo : prestamos) {

                            if (prestamo.isDevuelto()) {
                                prestamosDevueltos++;
                            } else {
                                prestamosPendientes++;
                            }
                        }

                        return new DatosReporte(
                                libros.size(),
                                ejemplaresDisponibles,
                                totalEstudiantes,
                                prestamosPendientes,
                                prestamosDevueltos,
                                prestamos
                        );
                    }

                    @Override
                    protected void done() {

                        vista.getBtnActualizar().setEnabled(true);
                        vista.getBtnActualizar().setText(
                                "Actualizar reportes"
                        );

                        try {

                            DatosReporte datos = get();

                            // MOSTRAR ESTADÍSTICAS
                            vista.getLblTotalLibros().setText(
                                    "Total de títulos: "
                                            + datos.totalLibros
                            );

                            vista.getLblEjemplaresDisponibles().setText(
                                    "Ejemplares disponibles: "
                                            + datos.ejemplaresDisponibles
                            );

                            vista.getLblTotalEstudiantes().setText(
                                    "Estudiantes registrados: "
                                            + datos.totalEstudiantes
                            );

                            vista.getLblPrestamosActivos().setText(
                                    "Préstamos pendientes: "
                                            + datos.prestamosPendientes
                            );

                            vista.getLblPrestamosDevueltos().setText(
                                    "Préstamos devueltos: "
                                            + datos.prestamosDevueltos
                            );

                            // MOSTRAR PRÉSTAMOS PENDIENTES
                            DefaultTableModel modelo =
                                    vista.getModeloTabla();

                            modelo.setRowCount(0);

                            for (Prestamo prestamo : datos.prestamos) {

                                if (!prestamo.isDevuelto()) {

                                    modelo.addRow(new Object[]{
                                            prestamo.getId(),
                                            prestamo.getIdEstudiante(),
                                            prestamo.getIdLibro(),
                                            prestamo.getFechaPrestamo(),
                                            "Pendiente"
                                    });
                                }
                            }

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();

                            mostrarError(
                                    "Se interrumpió la carga de reportes."
                            );

                        } catch (ExecutionException e) {

                            Throwable causa = e.getCause();

                            String mensaje = causa != null
                                    ? causa.getMessage()
                                    : e.getMessage();

                            mostrarError(
                                    "Error al generar reportes: "
                                            + mensaje
                            );
                        }
                    }
                };

        trabajador.execute();
    }

    // ========================================
    // DATOS DEL REPORTE
    // ========================================

    private static class DatosReporte {

        private final int totalLibros;
        private final int ejemplaresDisponibles;
        private final int totalEstudiantes;
        private final int prestamosPendientes;
        private final int prestamosDevueltos;

        private final List<Prestamo> prestamos;

        public DatosReporte(
                int totalLibros,
                int ejemplaresDisponibles,
                int totalEstudiantes,
                int prestamosPendientes,
                int prestamosDevueltos,
                List<Prestamo> prestamos
        ) {

            this.totalLibros = totalLibros;
            this.ejemplaresDisponibles = ejemplaresDisponibles;
            this.totalEstudiantes = totalEstudiantes;
            this.prestamosPendientes = prestamosPendientes;
            this.prestamosDevueltos = prestamosDevueltos;
            this.prestamos = prestamos;
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