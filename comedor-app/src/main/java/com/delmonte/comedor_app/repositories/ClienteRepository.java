package com.delmonte.comedor_app.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.delmonte.comedor_app.models.Cliente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByClaveEmpleado(String claveEmpleado);
}