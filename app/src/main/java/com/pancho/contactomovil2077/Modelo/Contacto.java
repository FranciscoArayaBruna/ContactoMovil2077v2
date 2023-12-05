package com.pancho.contactomovil2077.Modelo;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

public class Contacto implements Parcelable {
    private String apellido;
    private String correo;
    private String nombre;
    private String nombreUsuario;

    // Constructor vacío requerido para Firebase
    public Contacto() {
    }

    public Contacto(String apellido, String correo, String nombre, String nombreUsuario) {
        this.apellido = apellido;
        this.correo = correo;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
    }

    protected Contacto(Parcel in) {
        apellido = in.readString();
        correo = in.readString();
        nombre = in.readString();
        nombreUsuario = in.readString();
    }

    public static final Creator<Contacto> CREATOR = new Creator<Contacto>() {
        @Override
        public Contacto createFromParcel(Parcel in) {
            return new Contacto(in);
        }

        @Override
        public Contacto[] newArray(int size) {
            return new Contacto[size];
        }
    };


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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apellido);
        dest.writeString(correo);
        dest.writeString(nombre);
        dest.writeString(nombreUsuario);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Contacto contacto = (Contacto) obj;

        return correo.equals(contacto.correo);
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }
}
