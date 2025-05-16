package com.delmonte.comedor_app.controllers;


import com.delmonte.comedor_app.models.Usuario;
import com.delmonte.comedor_app.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/crear")
    public ResponseEntity<String> crearUsuario(@RequestBody Usuario usuario, HttpServletRequest request) {
        String nombre = (String) request.getSession().getAttribute("usuario");
        if (nombre == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (usuarioRepository.existsByNombre(usuario.getNombre())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de usuario ya está en uso");
        }

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario creado exitosamente");
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios(HttpServletRequest request) {
        String nombre = (String) request.getSession().getAttribute("usuario");
        if (nombre == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id, HttpServletRequest request) {
        String nombre = (String) request.getSession().getAttribute("usuario");
        if (nombre == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        usuarioRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<String> editarUsuario(@PathVariable Long id, @RequestBody Usuario usuario, HttpServletRequest request) {
        String nombre = (String) request.getSession().getAttribute("usuario");
        if (nombre == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario u = usuarioExistente.get();
            u.setNombre(usuario.getNombre());
            u.setPassword(usuario.getPassword());
            u.setRol(usuario.getRol());
            usuarioRepository.save(u);
            return ResponseEntity.ok("Usuario editado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }


}
