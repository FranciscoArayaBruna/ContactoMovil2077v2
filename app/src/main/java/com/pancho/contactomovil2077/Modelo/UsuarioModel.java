package com.pancho.contactomovil2077.Modelo;

import java.io.Serializable;

public class UsuarioModel implements Serializable {

    private String correo;
    private String contrasena;
    private String nombre;
    private String apellido;
    private String nombreUsuario;
    private String urlImagenPerfil;

    public UsuarioModel() {
        // Constructor vacío requerido por Firebase
    }

    public UsuarioModel(String correo, String contrasena, String nombre, String apellido, String nombreUsuario, String urlImagenPerfil) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreUsuario = nombreUsuario;
        this.urlImagenPerfil = urlImagenPerfil;
    }

    // Getters y setters según sea necesario

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getUrlImagenPerfil() {
        return urlImagenPerfil;
    }

    public void setUrlImagenPerfil(String urlImagenPerfil) {
        this.urlImagenPerfil = urlImagenPerfil;
    }
}
