package com.delmonte.comedor_app.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumenEmpleadoDTO {
    private String claveEmpleado;
    private String nombreCliente;
    private double totalGastado;
    private List<String> productosConsumidos = new ArrayList<>();

    // Constructor vacío (por si lo necesita Spring u otras herramientas)
    public ResumenEmpleadoDTO() {
    }

    // Constructor completo con parámetros
    public ResumenEmpleadoDTO(String claveEmpleado, String nombreCliente, double totalGastado, List<String> productosConsumidos) {
        this.claveEmpleado = claveEmpleado;
        this.nombreCliente = nombreCliente;
        this.totalGastado = totalGastado;
        this.productosConsumidos = productosConsumidos != null ? productosConsumidos : new ArrayList<>();
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

    public double getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(double totalGastado) {
        this.totalGastado = totalGastado;
    }

    public List<String> getProductosConsumidos() {
        return productosConsumidos;
    }

    public void setProductosConsumidos(List<String> productosConsumidos) {
        this.productosConsumidos = productosConsumidos;
    }

    // Método para agregar un producto individualmente
    public void agregarProductoConsumido(String productoNombre) {
        this.productosConsumidos.add(productoNombre);
    }
}

