package com.delmonte.comedor_app.dto;

import java.time.LocalDateTime;

public class PedidoDTO {
    private Long id;
    private String claveEmpleado;
    private String nombreCliente;
    private ProductoDTO producto; // 👈 Cambio aquí
    private Double total;
    private LocalDateTime fecha;
    private Long idCorte; // Opcional, si estás marcando pedidos cortados

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ProductoDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoDTO producto) {
        this.producto = producto;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(Long idCorte) {
        this.idCorte = idCorte;
    }
}
