package com.delmonte.comedor_app.controllers;

import com.delmonte.comedor_app.models.DetallePedido;
import com.delmonte.comedor_app.repositories.DetallePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/detallepedidos")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;


    // Crear un detalle de pedido(POST)
    @PostMapping
    public DetallePedido crearDetallePedido(@RequestBody DetallePedido detallePedido){
        return detallePedidoRepository.save(detallePedido);
    }

    //Obtener todos los detalles de pedido (Get)
    @GetMapping
    public List<DetallePedido> obtenerTodosLosDetallesDePedido() {
        return detallePedidoRepository.findAll();
    }

    //Obtener un detalle de pedido por ID (GET)
    @GetMapping("/{id}")
    public DetallePedido obtenerDetallePedidoPorId(@PathVariable Long id){
        return detallePedidoRepository.findById(id).orElse(null);
    }

    //Actualizar un detalle de pedido(Put)
    @PutMapping("/{id}")
    public DetallePedido actualizarDetallePedido(@PathVariable Long id, @RequestBody DetallePedido detallePedidoDetalles) {
        DetallePedido detallePedido = detallePedidoRepository.findById(id).orElse(null);
        if(detallePedido !=null) {
            detallePedido.setProducto(detallePedidoDetalles.getProducto());
            detallePedido.setCantidad(detallePedidoDetalles.getCantidad());
            detallePedido.setPrecioUnitario(detallePedidoDetalles.getPrecioUnitario());
            return detallePedidoRepository.save(detallePedido);
        }
        return null;
    }
    //Eliminar un detalle de pedido (DELETE)
    @DeleteMapping("/{id}")
    public String eliminarDetallePedido(@PathVariable Long id){
        detallePedidoRepository.deleteById(id);
        return "Detalle de pedido eliminado con éxito.";
    }
}
