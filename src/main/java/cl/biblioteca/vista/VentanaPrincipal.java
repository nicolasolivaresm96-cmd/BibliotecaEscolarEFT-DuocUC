package cl.biblioteca.vista;

import cl.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final JButton btnLibros;
    private final JButton btnEstudiantes;
    private final JButton btnPrestamos;
    private final JButton btnReportes;
    private final JButton btnCerrarSesion;

    public VentanaPrincipal(Usuario usuario) {

        setTitle("Biblioteca Escolar - Menú principal");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        JLabel titulo = new JLabel(
                "SISTEMA DE BIBLIOTECA ESCOLAR",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel bienvenida = new JLabel(
                "Bienvenido/a, " + usuario.getNombre()
                        + " | Rol: " + usuario.getRol(),
                SwingConstants.CENTER
        );

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 5, 5));
        encabezado.add(titulo);
        encabezado.add(bienvenida);

        panel.add(encabezado, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(4, 1, 12, 12));

        btnLibros = new JButton("Gestión de libros");
        btnEstudiantes = new JButton("Gestión de estudiantes");
        btnPrestamos = new JButton("Préstamos y devoluciones");
        btnReportes = new JButton("Reportes");

        menu.add(btnLibros);
        menu.add(btnEstudiantes);
        menu.add(btnPrestamos);
        menu.add(btnReportes);

        panel.add(menu, BorderLayout.CENTER);

        btnCerrarSesion = new JButton("Cerrar sesión");
        panel.add(btnCerrarSesion, BorderLayout.SOUTH);

        // Los estudiantes tendrán acceso limitado.
        if (!usuario.esBibliotecario()) {
            btnEstudiantes.setEnabled(false);
            btnPrestamos.setEnabled(false);
        }

        add(panel);
    }

    public JButton getBtnLibros() {
        return btnLibros;
    }

    public JButton getBtnEstudiantes() {
        return btnEstudiantes;
    }

    public JButton getBtnPrestamos() {
        return btnPrestamos;
    }

    public JButton getBtnReportes() {
        return btnReportes;
    }

    public JButton getBtnCerrarSesion() {
        return btnCerrarSesion;
    }
}