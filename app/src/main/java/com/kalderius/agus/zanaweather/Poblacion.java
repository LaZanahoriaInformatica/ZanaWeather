package com.kalderius.agus.zanaweather;

/**
 * Created by david on 15/01/2018.
 */

public class Poblacion {
    private String id;
    private String Nombre;

    public Poblacion(String id, String nombre) {
        this.id = id;
        Nombre = nombre;
    }

    public Poblacion() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    @Override
    public String toString() {
        return "Poblacion con Nombre='" + Nombre + "'";
    }
}
