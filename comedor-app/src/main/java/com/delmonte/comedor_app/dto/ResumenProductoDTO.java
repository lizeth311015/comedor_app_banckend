package com.delmonte.comedor_app.dto;

public class ResumenProductoDTO {
    private String nombre;
    private int cantidad;

    // Constructor vacío
    public ResumenProductoDTO() {}

    // Constructor con parámetros
    public ResumenProductoDTO(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "ResumenProductoDTO{" +
                "nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}

