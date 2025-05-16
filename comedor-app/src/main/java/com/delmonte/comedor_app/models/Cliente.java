package com.delmonte.comedor_app.models;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String claveEmpleado;
    private double saldo;

    // Constructor vacío
    public Cliente() {}

    // Constructor con parametros
    public Cliente(String nombre, String claveEmpleado, double saldo) {
        this.nombre = nombre;
        this.claveEmpleado = claveEmpleado;
        this.saldo = saldo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClaveEmpleado() {
        return claveEmpleado;
    }

    public void setClaveEmpleado(String claveEmpleado) {
        this.claveEmpleado = claveEmpleado;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
