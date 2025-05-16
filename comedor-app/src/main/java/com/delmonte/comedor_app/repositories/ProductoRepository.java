package com.delmonte.comedor_app.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.delmonte.comedor_app.models.Producto;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // aquí puedes añadir consultas personalizadas si las necesitas
}