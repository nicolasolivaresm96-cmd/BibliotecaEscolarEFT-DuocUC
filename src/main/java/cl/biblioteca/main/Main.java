package cl.biblioteca.main;

import cl.biblioteca.controlador.LoginControlador;
import cl.biblioteca.vista.VentanaLogin;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaLogin ventana = new VentanaLogin();

            new LoginControlador(ventana);

            ventana.setVisible(true);
        });
    }
}