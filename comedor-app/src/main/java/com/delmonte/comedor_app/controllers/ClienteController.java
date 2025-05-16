package com.delmonte.comedor_app.controllers;

import com.delmonte.comedor_app.models.Cliente;
import com.delmonte.comedor_app.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:3000") // Permite acceso desde el frontend
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // Crear un cliente (POST)
    @PostMapping
    public Cliente crearCliente(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Obtener todos los clientes (GET)
    @GetMapping
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    // Obtener un cliente por ID (GET)
    @GetMapping("/{id}")
    public Cliente obtenerClientePorId(@PathVariable Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    // Actualizar un cliente (PUT)
    @PutMapping("/{id}")
    public Cliente actualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteDetalles) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente != null) {
            cliente.setNombre(clienteDetalles.getNombre());
            cliente.setClaveEmpleado(clienteDetalles.getClaveEmpleado());
            cliente.setSaldo(clienteDetalles.getSaldo());
            return clienteRepository.save(cliente);
        }
        return null;
    }

    // Eliminar un cliente (DELETE)
    @DeleteMapping("/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteRepository.deleteById(id);
        return "Cliente eliminado con éxito.";
    }
    // Buscar cliente por clave de empleado (para escaneo)
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/buscar/{claveEmpleado}")
    public ResponseEntity<Cliente> obtenerClientePorClaveEmpleado(@PathVariable String claveEmpleado) {
        Cliente cliente = clienteRepository.findByClaveEmpleado(claveEmpleado);
        if (cliente != null) {
            return ResponseEntity.ok(cliente); // Cliente encontrado
        } else {
            return ResponseEntity.status(404).body(null); // Cliente no encontrado
        }
    }
}