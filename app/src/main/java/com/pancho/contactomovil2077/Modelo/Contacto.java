package com.pancho.contactomovil2077.Modelo;

public class Contacto {
    private String apellido;
    private String correo;
    private String nombre;
    private String nombreUsuario;

    // Necesario para Firebase
    public Contacto() {
        // Constructor vacío requerido para Firebase
    }

    public Contacto(String apellido, String contrasena, String correo, String nombre, String nombreUsuario) {
        this.apellido = apellido;
        this.correo = correo;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellido() {
        return apellido;
    }


    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
}
