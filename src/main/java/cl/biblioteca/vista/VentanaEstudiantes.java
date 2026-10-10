package cl.biblioteca.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaEstudiantes extends JFrame {

    private final JTable tablaEstudiantes;
    private final DefaultTableModel modeloTabla;

    private final JButton btnAgregar;
    private final JButton btnEditar;
    private final JButton btnEliminar;
    private final JButton btnActualizar;

    public VentanaEstudiantes() {

        setTitle("Biblioteca Escolar - Gestión de estudiantes");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 15));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel titulo = new JLabel(
                "GESTIÓN DE ESTUDIANTES",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(titulo, BorderLayout.NORTH);

        String[] columnas = {
                "ID", "Nombre", "RUT", "Curso", "Correo"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaEstudiantes = new JTable(modeloTabla);

        tablaEstudiantes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaEstudiantes.setRowHeight(28);
        tablaEstudiantes.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(tablaEstudiantes);

        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(
                new GridLayout(1, 4, 10, 10)
        );

        btnAgregar = new JButton("Agregar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar lista");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        add(panel);
    }

    public JTable getTablaEstudiantes() {
        return tablaEstudiantes;
    }

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }
}