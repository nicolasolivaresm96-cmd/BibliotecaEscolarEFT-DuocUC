package cl.biblioteca.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaPrestamos extends JFrame {

    private final JTable tablaPrestamos;
    private final DefaultTableModel modeloTabla;

    private final JButton btnRegistrar;
    private final JButton btnDevolver;
    private final JButton btnActualizar;

    public VentanaPrestamos() {

        setTitle("Biblioteca Escolar - Préstamos y devoluciones");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // TÍTULO
        JLabel titulo = new JLabel(
                "GESTIÓN DE PRÉSTAMOS Y DEVOLUCIONES",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 21));

        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // TABLA DE PRÉSTAMOS
        String[] columnas = {
                "ID",
                "ID Estudiante",
                "ID Libro",
                "Fecha préstamo",
                "Fecha devolución",
                "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.setRowHeight(28);
        tablaPrestamos.setAutoCreateRowSorter(true);
        tablaPrestamos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll = new JScrollPane(tablaPrestamos);
        panelPrincipal.add(scroll, BorderLayout.CENTER);

        // BOTONES
        JPanel panelBotones = new JPanel(
                new GridLayout(1, 3, 12, 10)
        );

        btnRegistrar = new JButton("Registrar préstamo");
        btnDevolver = new JButton("Registrar devolución");
        btnActualizar = new JButton("Actualizar lista");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnDevolver);
        panelBotones.add(btnActualizar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    public JTable getTablaPrestamos() {
        return tablaPrestamos;
    }

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    public JButton getBtnRegistrar() {
        return btnRegistrar;
    }

    public JButton getBtnDevolver() {
        return btnDevolver;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }
}