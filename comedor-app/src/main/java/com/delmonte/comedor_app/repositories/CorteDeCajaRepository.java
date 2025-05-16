package com.delmonte.comedor_app.repositories;

import com.delmonte.comedor_app.models.CorteDeCaja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorteDeCajaRepository extends JpaRepository<CorteDeCaja, Long> {
}