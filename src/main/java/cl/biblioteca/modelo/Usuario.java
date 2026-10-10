package cl.biblioteca.modelo;

public class Usuario {

    private int id;
    private String nombre;
    private String rut;
    private String correo;
    private String rol;

    public Usuario(int id, String nombre, String rut,
                   String correo, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRut() {
        return rut;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public boolean esBibliotecario() {
        return "bibliotecario".equalsIgnoreCase(rol);
    }
}