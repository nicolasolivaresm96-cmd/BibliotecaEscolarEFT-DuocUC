package cl.biblioteca.vista;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {

    private JTextField txtRut;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;

    public VentanaLogin() {

        setTitle("Biblioteca Escolar - Inicio de sesión");
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        JLabel titulo = new JLabel(
                "SISTEMA DE BIBLIOTECA ESCOLAR",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridy = 0;
        panel.add(titulo, gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("RUT:"), gbc);

        txtRut = new JTextField(20);
        gbc.gridy = 2;
        panel.add(txtRut, gbc);

        gbc.gridy = 3;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtContrasena = new JPasswordField(20);
        gbc.gridy = 4;
        panel.add(txtContrasena, gbc);

        btnIngresar = new JButton("Iniciar sesión");
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(btnIngresar, gbc);

        add(panel);
        getRootPane().setDefaultButton(btnIngresar);
    }

    public String getRut() {
        return txtRut.getText().trim();
    }

    public char[] getContrasena() {
        return txtContrasena.getPassword();
    }

    public JButton getBtnIngresar() {
        return btnIngresar;
    }
}