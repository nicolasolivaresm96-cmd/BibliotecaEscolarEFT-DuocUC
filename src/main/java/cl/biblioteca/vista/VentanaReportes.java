package cl.biblioteca.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaReportes extends JFrame {

    private final JLabel lblTotalLibros;
    private final JLabel lblEjemplaresDisponibles;
    private final JLabel lblTotalEstudiantes;
    private final JLabel lblPrestamosActivos;
    private final JLabel lblPrestamosDevueltos;

    private final JTable tablaPrestamos;
    private final DefaultTableModel modeloTabla;

    private final JButton btnActualizar;

    public VentanaReportes() {

        setTitle("Biblioteca Escolar - Reportes");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // TÍTULO
        JLabel titulo = new JLabel(
                "REPORTES DE LA BIBLIOTECA",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // PANEL DE ESTADÍSTICAS
        JPanel panelEstadisticas = new JPanel(
                new GridLayout(5, 1, 8, 8)
        );

        panelEstadisticas.setBorder(
                BorderFactory.createTitledBorder(
                        "Resumen general"
                )
        );

        lblTotalLibros = new JLabel("Total de títulos: -");
        lblEjemplaresDisponibles = new JLabel(
                "Ejemplares disponibles: -"
        );
        lblTotalEstudiantes = new JLabel(
                "Estudiantes registrados: -"
        );
        lblPrestamosActivos = new JLabel(
                "Préstamos pendientes: -"
        );
        lblPrestamosDevueltos = new JLabel(
                "Préstamos devueltos: -"
        );

        Font fuente = new Font("Arial", Font.PLAIN, 15);

        lblTotalLibros.setFont(fuente);
        lblEjemplaresDisponibles.setFont(fuente);
        lblTotalEstudiantes.setFont(fuente);
        lblPrestamosActivos.setFont(fuente);
        lblPrestamosDevueltos.setFont(fuente);

        panelEstadisticas.add(lblTotalLibros);
        panelEstadisticas.add(lblEjemplaresDisponibles);
        panelEstadisticas.add(lblTotalEstudiantes);
        panelEstadisticas.add(lblPrestamosActivos);
        panelEstadisticas.add(lblPrestamosDevueltos);

        // TABLA DE PRÉSTAMOS PENDIENTES
        String[] columnas = {
                "ID préstamo",
                "ID estudiante",
                "ID libro",
                "Fecha préstamo",
                "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.setRowHeight(27);
        tablaPrestamos.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(tablaPrestamos);
        scroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Detalle de préstamos pendientes"
                )
        );

        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.add(panelEstadisticas, BorderLayout.NORTH);
        panelCentro.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // BOTÓN ACTUALIZAR
        btnActualizar = new JButton("Actualizar reportes");

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        panelInferior.add(btnActualizar);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    public JLabel getLblTotalLibros() {
        return lblTotalLibros;
    }

    public JLabel getLblEjemplaresDisponibles() {
        return lblEjemplaresDisponibles;
    }

    public JLabel getLblTotalEstudiantes() {
        return lblTotalEstudiantes;
    }

    public JLabel getLblPrestamosActivos() {
        return lblPrestamosActivos;
    }

    public JLabel getLblPrestamosDevueltos() {
        return lblPrestamosDevueltos;
    }

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    public JTable getTablaPrestamos() {
        return tablaPrestamos;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }
}