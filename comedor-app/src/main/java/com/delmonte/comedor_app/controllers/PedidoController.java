package com.delmonte.comedor_app.controllers;

import com.delmonte.comedor_app.dto.PedidoDTO;
import com.delmonte.comedor_app.models.Cliente;
import com.delmonte.comedor_app.models.Pedido;
import com.delmonte.comedor_app.models.Producto;
import com.delmonte.comedor_app.repositories.ClienteRepository;
import com.delmonte.comedor_app.repositories.PedidoRepository;
import com.delmonte.comedor_app.repositories.ProductoRepository;
import com.delmonte.comedor_app.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public PedidoController(PedidoService pedidoService,
                            PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProductoRepository productoRepository) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrarPedido(
            @RequestParam String claveEmpleado,
            @RequestParam Long productoId) {

        Cliente cliente = clienteRepository.findByClaveEmpleado(claveEmpleado);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Cliente no encontrado"));
        }

        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Producto no encontrado"));
        }

        // Actualiza saldo
        cliente.setSaldo(cliente.getSaldo() + producto.getPrecio());
        clienteRepository.save(cliente);

        // Crea y guarda pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProducto(producto);
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(producto.getPrecio());
        pedido.setClaveEmpleado(cliente.getClaveEmpleado());
        pedido.setNombreCliente(cliente.getNombre());
        pedidoRepository.save(pedido);

        // Respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Pedido registrado correctamente");
        response.put("nuevoSaldo", cliente.getSaldo());
        response.put("pedidoId", pedido.getId());
        response.put("fecha", pedido.getFecha().toString()); // o formateado
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{claveEmpleado}")
    public ResponseEntity<List<PedidoDTO>> obtenerPedidosPorCliente(
            @PathVariable String claveEmpleado) {
        List<PedidoDTO> pedidos = pedidoService.obtenerPedidosPorCliente(claveEmpleado);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<PedidoDTO>> obtenerTodosLosPedidos() {
        List<PedidoDTO> pedidos = pedidoService.obtenerTodosLosPedidos(); // Nombre corregido
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/reporte")
    public ResponseEntity<List<PedidoDTO>> obtenerReporteCorteCaja(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<PedidoDTO> reporte = pedidoService.obtenerPedidosEntreFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    @DeleteMapping("/eliminar/{pedidoId}")
    public ResponseEntity<Map<String, Object>> eliminarPedido(
            @PathVariable Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Pedido no encontrado"));
        }

        Cliente cliente = pedido.getCliente();
        pedidoRepository.deleteById(pedidoId);

        // Recalcula saldo
        double nuevoSaldo = pedidoRepository.findByCliente(cliente)
                .stream()
                .mapToDouble(Pedido::getTotal)
                .sum();
        cliente.setSaldo(nuevoSaldo);
        clienteRepository.save(cliente);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido eliminado y saldo actualizado",
                "nuevoSaldo", nuevoSaldo
        ));
    }
}
