package com.delmonte.comedor_app.dto;

import java.time.LocalDateTime;

public class DetalleConsumoDTO {
    private String claveEmpleado;
    private String nombreCliente;
    private String productoNombre;
    private double total;
    private LocalDateTime fecha;

    // Constructor vacío
    public DetalleConsumoDTO() {
    }

    // Constructor con parámetros
    public DetalleConsumoDTO(String claveEmpleado, String nombreCliente, String productoNombre, double total, LocalDateTime fecha) {
        this.claveEmpleado = claveEmpleado;
        this.nombreCliente = nombreCliente;
        this.productoNombre = productoNombre;
        this.total = total;
        this.fecha = fecha;
    }

    // Getters y setters
    public String getClaveEmpleado() {
        return claveEmpleado;
    }

    public void setClaveEmpleado(String claveEmpleado) {
        this.claveEmpleado = claveEmpleado;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
