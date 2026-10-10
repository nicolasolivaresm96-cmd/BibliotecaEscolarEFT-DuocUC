package cl.biblioteca.controlador;

import cl.biblioteca.datos.UsuarioDAO;
import cl.biblioteca.modelo.Usuario;

import cl.biblioteca.vista.VentanaLogin;
import cl.biblioteca.vista.VentanaPrincipal;
import cl.biblioteca.vista.VentanaLibros;
import cl.biblioteca.vista.VentanaEstudiantes;
import cl.biblioteca.vista.VentanaPrestamos;
import cl.biblioteca.vista.VentanaReportes;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginControlador {

    private final VentanaLogin vista;
    private final UsuarioDAO usuarioDAO;

    public LoginControlador(VentanaLogin vista) {

        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();

        vista.getBtnIngresar().addActionListener(
                e -> iniciarSesion()
        );
    }

    // ========================================
    // INICIAR SESIÓN
    // ========================================

    private void iniciarSesion() {

        String rut = vista.getRut();
        char[] clave = vista.getContrasena();

        if (rut.isBlank() || clave.length == 0) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Debes ingresar RUT y contraseña."
            );

            Arrays.fill(clave, '\0');
            return;
        }

        try {

            Usuario usuario = usuarioDAO.autenticar(
                    rut,
                    new String(clave)
            );

            if (usuario == null) {

                JOptionPane.showMessageDialog(
                        vista,
                        "RUT o contraseña incorrectos.",
                        "Acceso denegado",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // ABRIR MENÚ PRINCIPAL
            VentanaPrincipal principal =
                    new VentanaPrincipal(usuario);

            configurarMenu(principal, usuario);

            vista.dispose();
            principal.setVisible(true);

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Error al iniciar sesión: " + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            Arrays.fill(clave, '\0');
        }
    }

    // ========================================
    // CONECTAR MENÚ PRINCIPAL
    // ========================================

    private void configurarMenu(
            VentanaPrincipal principal,
            Usuario usuario
    ) {

        // GESTIÓN DE LIBROS
        principal.getBtnLibros().addActionListener(e -> {

            VentanaLibros ventana = new VentanaLibros();

            new LibroControlador(ventana);

            // Los estudiantes pueden consultar,
            // pero no modificar el catálogo.
            if (!usuario.esBibliotecario()) {

                ventana.getBtnAgregar().setEnabled(false);
                ventana.getBtnEditar().setEnabled(false);
                ventana.getBtnEliminar().setEnabled(false);
            }

            ventana.setVisible(true);
        });

        // GESTIÓN DE ESTUDIANTES
        principal.getBtnEstudiantes().addActionListener(e -> {

            if (!usuario.esBibliotecario()) {
                mostrarAccesoDenegado(principal);
                return;
            }

            VentanaEstudiantes ventana =
                    new VentanaEstudiantes();

            new EstudianteControlador(ventana);

            ventana.setVisible(true);
        });

        // PRÉSTAMOS Y DEVOLUCIONES
        principal.getBtnPrestamos().addActionListener(e -> {

            if (!usuario.esBibliotecario()) {
                mostrarAccesoDenegado(principal);
                return;
            }

            VentanaPrestamos ventana =
                    new VentanaPrestamos();

            new PrestamoControlador(ventana);

            ventana.setVisible(true);
        });

        // REPORTES
        principal.getBtnReportes().addActionListener(e -> {

            VentanaReportes ventana =
                    new VentanaReportes();

            new ReporteControlador(ventana);

            ventana.setVisible(true);
        });

        // CERRAR SESIÓN
        principal.getBtnCerrarSesion().addActionListener(e -> {

            int confirmacion = JOptionPane.showConfirmDialog(
                    principal,
                    "¿Deseas cerrar tu sesión?",
                    "Cerrar sesión",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            // Cerrar ventanas secundarias abiertas
            for (java.awt.Window ventana : java.awt.Window.getWindows()) {

                if (ventana instanceof VentanaLibros
                        || ventana instanceof VentanaEstudiantes
                        || ventana instanceof VentanaPrestamos
                        || ventana instanceof VentanaReportes) {

                    ventana.dispose();
                }
            }

            principal.dispose();

            VentanaLogin nuevoLogin = new VentanaLogin();

            new LoginControlador(nuevoLogin);

            nuevoLogin.setVisible(true);
        });
    }

    // ========================================
    // CONTROL DE ACCESO
    // ========================================

    private void mostrarAccesoDenegado(JFrame ventana) {

        JOptionPane.showMessageDialog(
                ventana,
                "No tienes permisos para acceder a este módulo.",
                "Acceso denegado",
                JOptionPane.WARNING_MESSAGE
        );
    }
}