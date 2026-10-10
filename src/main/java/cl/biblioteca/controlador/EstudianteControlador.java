package cl.biblioteca.controlador;

import cl.biblioteca.datos.EstudianteDAO;
import cl.biblioteca.modelo.Estudiante;
import cl.biblioteca.vista.VentanaEstudiantes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class EstudianteControlador {

    private final VentanaEstudiantes vista;
    private final EstudianteDAO estudianteDAO;

    public EstudianteControlador(VentanaEstudiantes vista) {

        this.vista = vista;
        this.estudianteDAO = new EstudianteDAO();

        // CONECTAR BOTONES
        vista.getBtnAgregar().addActionListener(e -> agregarEstudiante());
        vista.getBtnEditar().addActionListener(e -> editarEstudiante());
        vista.getBtnEliminar().addActionListener(e -> eliminarEstudiante());
        vista.getBtnActualizar().addActionListener(e -> cargarEstudiantes());

        // CARGAR ESTUDIANTES AL ABRIR
        cargarEstudiantes();
    }

    // ========================================
    // LISTAR ESTUDIANTES
    // ========================================

    public void cargarEstudiantes() {

        try {
            List<Estudiante> estudiantes = estudianteDAO.listar();
            DefaultTableModel modelo = vista.getModeloTabla();

            modelo.setRowCount(0);

            for (Estudiante estudiante : estudiantes) {

                modelo.addRow(new Object[]{
                        estudiante.getId(),
                        estudiante.getNombre(),
                        estudiante.getRut(),
                        estudiante.getCurso(),
                        estudiante.getCorreo()
                });
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar estudiantes: " + e.getMessage());
        }
    }

    // ========================================
    // AGREGAR ESTUDIANTE
    // ========================================

    private void agregarEstudiante() {

        Estudiante nuevo = mostrarFormulario(null);

        if (nuevo == null) {
            return;
        }

        try {
            estudianteDAO.crear(nuevo);

            JOptionPane.showMessageDialog(
                    vista,
                    "Estudiante agregado correctamente."
            );

            cargarEstudiantes();

        } catch (SQLException e) {
            mostrarError("Error al agregar estudiante: " + e.getMessage());
        }
    }

    // ========================================
    // EDITAR ESTUDIANTE
    // ========================================

    private void editarEstudiante() {

        int fila = vista.getTablaEstudiantes().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Selecciona un estudiante para editar."
            );
            return;
        }

        int filaModelo = vista.getTablaEstudiantes()
                .convertRowIndexToModel(fila);

        DefaultTableModel modelo = vista.getModeloTabla();

        int id = (int) modelo.getValueAt(filaModelo, 0);

        Estudiante actual = new Estudiante(
                id,
                modelo.getValueAt(filaModelo, 1).toString(),
                modelo.getValueAt(filaModelo, 2).toString(),
                modelo.getValueAt(filaModelo, 3).toString(),
                modelo.getValueAt(filaModelo, 4).toString()
        );

        Estudiante actualizado = mostrarFormulario(actual);

        if (actualizado == null) {
            return;
        }

        try {
            boolean resultado = estudianteDAO.actualizar(actualizado);

            if (resultado) {
                JOptionPane.showMessageDialog(
                        vista,
                        "Estudiante actualizado correctamente."
                );
                cargarEstudiantes();
            } else {
                mostrarError("No se encontró el estudiante.");
            }

        } catch (SQLException e) {
            mostrarError("Error al actualizar estudiante: " + e.getMessage());
        }
    }

    // ========================================
    // ELIMINAR ESTUDIANTE
    // ========================================

    private void eliminarEstudiante() {

        int fila = vista.getTablaEstudiantes().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Selecciona un estudiante para eliminar."
            );
            return;
        }

        int filaModelo = vista.getTablaEstudiantes()
                .convertRowIndexToModel(fila);

        DefaultTableModel modelo = vista.getModeloTabla();

        int id = (int) modelo.getValueAt(filaModelo, 0);
        String nombre = modelo.getValueAt(filaModelo, 1).toString();

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Estás seguro de eliminar al estudiante?\n\n" + nombre,
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = estudianteDAO.eliminar(id);

            if (eliminado) {
                JOptionPane.showMessageDialog(
                        vista,
                        "Estudiante eliminado correctamente."
                );
                cargarEstudiantes();
            } else {
                mostrarError("No se encontró el estudiante.");
            }

        } catch (SQLException e) {
            mostrarError(
                    "No se pudo eliminar al estudiante.\n" +
                            "Puede tener préstamos asociados.\n\n" +
                            e.getMessage()
            );
        }
    }

    // ========================================
    // FORMULARIO AGREGAR / EDITAR
    // ========================================

    private Estudiante mostrarFormulario(Estudiante estudiante) {

        boolean esEdicion = estudiante != null;

        JTextField txtNombre = new JTextField(
                esEdicion ? estudiante.getNombre() : ""
        );

        JTextField txtRut = new JTextField(
                esEdicion ? estudiante.getRut() : ""
        );

        JTextField txtCurso = new JTextField(
                esEdicion ? estudiante.getCurso() : ""
        );

        JTextField txtCorreo = new JTextField(
                esEdicion ? estudiante.getCorreo() : ""
        );

        Object[] campos = {
                "Nombre:", txtNombre,
                "RUT:", txtRut,
                "Curso:", txtCurso,
                "Correo:", txtCorreo
        };

        while (true) {

            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    campos,
                    esEdicion ? "Editar estudiante" : "Agregar estudiante",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }

            try {
                String nombre = txtNombre.getText().trim();
                String rut = txtRut.getText().trim();
                String curso = txtCurso.getText().trim();
                String correo = txtCorreo.getText().trim();

                if (nombre.isEmpty() || rut.isEmpty()
                        || curso.isEmpty() || correo.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Todos los campos son obligatorios."
                    );
                }

                if (nombre.length() > 100
                        || rut.length() > 12
                        || curso.length() > 20
                        || correo.length() > 100) {

                    throw new IllegalArgumentException(
                            "Uno o más campos superan el largo permitido."
                    );
                }

                if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    throw new IllegalArgumentException(
                            "Ingresa un correo electrónico válido."
                    );
                }

                int id = esEdicion ? estudiante.getId() : 0;

                return new Estudiante(
                        id,
                        nombre,
                        rut,
                        curso,
                        correo
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