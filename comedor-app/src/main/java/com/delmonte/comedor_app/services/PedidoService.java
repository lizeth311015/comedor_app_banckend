package com.delmonte.comedor_app.services;

import com.delmonte.comedor_app.dto.ProductoDTO;
import com.delmonte.comedor_app.dto.PedidoDTO;
import com.delmonte.comedor_app.models.Pedido;
import com.delmonte.comedor_app.models.Producto;
import com.delmonte.comedor_app.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Obtener todos los pedidos
    public List<PedidoDTO> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener pedidos por cliente
    public List<PedidoDTO> obtenerPedidosPorCliente(String claveEmpleado) {
        List<Pedido> pedidos = pedidoRepository.findByClaveEmpleado(claveEmpleado);
        return pedidos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener pedidos entre fechas
    public List<PedidoDTO> obtenerPedidosEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Pedido> pedidos = pedidoRepository.findByFechaBetween(fechaInicio, fechaFin);
        return pedidos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Conversor Pedido → PedidoDTO
    private PedidoDTO toDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setClaveEmpleado(pedido.getCliente().getClaveEmpleado());
        dto.setNombreCliente(pedido.getCliente().getNombre()); // <- Usa getNombreCompleto()
        dto.setProducto(toProductoDTO(pedido.getProducto()));
        dto.setTotal(pedido.getTotal());
        dto.setFecha(pedido.getFecha());

        // Asegúrate de que 'idCorte' esté presente en el modelo Pedido y en PedidoDTO
        if (pedido.getIdCorte() != null) {
            dto.setIdCorte(pedido.getIdCorte());
        }

        return dto;
    }

    private ProductoDTO toProductoDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setImagen(producto.getImagen());
        return dto;
    }
}
