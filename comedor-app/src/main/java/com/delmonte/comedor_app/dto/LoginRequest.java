package com.delmonte.comedor_app.dto;

import com.delmonte.comedor_app.models.Usuario;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class LoginRequest {
    private String nombre;
    private String password;
    @Enumerated(EnumType.STRING)
    private Usuario.Rol rol;

    public Usuario.Rol getRol() {
        return rol;
    }

    public void setRol(Usuario.Rol rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
