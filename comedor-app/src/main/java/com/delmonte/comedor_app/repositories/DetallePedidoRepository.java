package com.delmonte.comedor_app.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.delmonte.comedor_app.models.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}