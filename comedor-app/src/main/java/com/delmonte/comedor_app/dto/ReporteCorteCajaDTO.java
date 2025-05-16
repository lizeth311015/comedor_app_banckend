// src/main/java/com/delmonte/comedor_app/dto/ReporteCorteCajaDTO.java
package com.delmonte.comedor_app.dto;

import java.util.List;

public class ReporteCorteCajaDTO {
    private String claveEmpleado;
    private String nombreCliente;
    private Double totalGastado;
    private List<String> productosConsumidos;

    // Constructores
    public ReporteCorteCajaDTO(String claveEmpleado, String nombreCliente, Double totalGastado, List<String> productosConsumidos) {
        this.claveEmpleado = claveEmpleado;
        this.nombreCliente = nombreCliente;
        this.totalGastado = totalGastado;
        this.productosConsumidos = productosConsumidos;
    }

    // Getters y Setters
    public String getClaveEmpleado() { return claveEmpleado; }
    public void setClaveEmpleado(String claveEmpleado) { this.claveEmpleado = claveEmpleado; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public Double getTotalGastado() { return totalGastado; }
    public void setTotalGastado(Double totalGastado) { this.totalGastado = totalGastado; }

    public List<String> getProductosConsumidos() { return productosConsumidos; }
    public void setProductosConsumidos(List<String> productosConsumidos) { this.productosConsumidos = productosConsumidos; }
}
