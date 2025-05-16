package com.delmonte.comedor_app.repositories;

import com.delmonte.comedor_app.models.Cliente;
import com.delmonte.comedor_app.models.CorteDeCaja;
import com.delmonte.comedor_app.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {



    List<Pedido> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.claveEmpleado = :claveEmpleado")
    List<Pedido> obtenerPedidosPorClaveEmpleado(String claveEmpleado);
    public List<Pedido> findByCliente(Cliente cliente);
    //List<Pedido> findByCorteIsNull(); // trae todos los pedidos aún no asociados a un corte
    List<Pedido> findByCorte(CorteDeCaja corte);
    List<Pedido> findByCorteIsNull();

    //List<Pedido> findByClaveEmpleado(String claveEmpleado);


    List<Pedido> findByClaveEmpleado(String claveEmpleado);
}
