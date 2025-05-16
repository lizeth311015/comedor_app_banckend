package com.delmonte.comedor_app.controllers;

import com.delmonte.comedor_app.dto.LoginRequest;
import com.delmonte.comedor_app.models.Usuario;
import com.delmonte.comedor_app.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByNombre(loginRequest.getNombre());
        Usuario usuario = optionalUsuario.orElse(null);

        if (usuario == null || !usuario.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
        }

        // Crear o recuperar la sesión actual
        HttpSession session = request.getSession(true);
        session.setAttribute("usuario", usuario); // Guardamos el usuario autenticado en la sesión

        // Devolvemos solo datos relevantes
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", usuario.getNombre());
        datos.put("rol", usuario.getRol().name()); // Convertimos el enum a String

        return ResponseEntity.ok(datos);
    }


    @GetMapping("/usuario-actual")
    public ResponseEntity<?> obtenerUsuarioActual(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", usuario.getNombre());
        datos.put("rol", usuario.getRol().name());

        return ResponseEntity.ok(datos);
    }




    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Sesión cerrada");
    }


    // LISTAR USUARIOS (solo si eres ADMIN)
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios(HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (usuario == null || usuario.getRol() != Usuario.Rol.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(usuarioRepository.findAll());
    }



    // CREAR USUARIO (solo si eres ADMIN)
    @PostMapping("/crear-usuario")
    public ResponseEntity<String> crearUsuario(@RequestBody Usuario nuevoUsuario, HttpServletRequest request) {
        Usuario usuarioActual = getUsuarioDesdeSesion(request);
        if (usuarioActual == null || usuarioActual.getRol() != Usuario.Rol.ADMIN) {
            return ResponseEntity.status(403).body("No tienes permisos");
        }

        if (usuarioRepository.findByNombre(nuevoUsuario.getNombre()).isPresent()) {
            return ResponseEntity.badRequest().body("Nombre de usuario ya existe");
        }

        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok("Usuario creado");
    }
    // ELIMINAR USUARIO (solo si eres ADMIN)
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id, HttpServletRequest request) {
        Usuario usuarioActual = getUsuarioDesdeSesion(request);
        if (usuarioActual == null || usuarioActual.getRol() != Usuario.Rol.ADMIN) {
            return ResponseEntity.status(403).body("No tienes permisos");
        }

        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado");
    }

    // EDITAR USUARIO (solo si eres ADMIN)
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<String> editarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuarioEditado,
            HttpServletRequest request
    ) {
        Usuario usuarioActual = getUsuarioDesdeSesion(request);
        if (usuarioActual == null || usuarioActual.getRol() != Usuario.Rol.ADMIN) {
            return ResponseEntity.status(403).body("No tienes permisos");
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuarioExistente = optionalUsuario.get();

        // Actualizamos los campos permitidos
        usuarioExistente.setNombre(usuarioEditado.getNombre());
        usuarioExistente.setPassword(usuarioEditado.getPassword());
        usuarioExistente.setRol(usuarioEditado.getRol());

        usuarioRepository.save(usuarioExistente);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }



    private Usuario getUsuarioDesdeSesion(HttpServletRequest request) {
        return (Usuario) request.getSession().getAttribute("usuario");
    }


}

