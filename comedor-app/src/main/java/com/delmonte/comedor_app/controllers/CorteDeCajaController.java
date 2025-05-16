package com.delmonte.comedor_app.controllers;

import com.delmonte.comedor_app.dto.ResumenEmpleadoDTO;
import com.delmonte.comedor_app.models.ArchivoCorte;
import com.delmonte.comedor_app.models.CorteDeCaja;
import com.delmonte.comedor_app.models.Pedido;
import com.delmonte.comedor_app.repositories.ArchivoCorteRepository;
import com.delmonte.comedor_app.repositories.CorteDeCajaRepository;
import com.delmonte.comedor_app.repositories.PedidoRepository;
import com.delmonte.comedor_app.services.CorteCajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/corte-de-caja")
@CrossOrigin(origins = "http://localhost:3000")
public class CorteDeCajaController {

    @Autowired
    private CorteCajaService corteCajaService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CorteDeCajaRepository corteCajaRepository;

    @Autowired
    private ArchivoCorteRepository archivoCorteRepository;


    @GetMapping("/reporte-agrupado")
    public List<ResumenEmpleadoDTO> obtenerReporteAgrupado() {
        return corteCajaService.generarResumenYEliminarPedidos();
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarCorteCaja() {
        try {
            corteCajaService.generarYGuardarCorte(); // <-- Aquí puede estar fallando
            return ResponseEntity.ok("Corte de caja generado correctamente");
        } catch (Exception e) {
            e.printStackTrace(); // Verás el error en la consola del backend
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el corte de caja");
        }
    }



    @GetMapping("/guardados")
    public List<CorteDeCaja> obtenerCortesGuardados() {
        return corteCajaRepository.findAll();
    }

    @GetMapping("/detalle/{id}")
    public List<ResumenEmpleadoDTO> obtenerDetalleCorte(@PathVariable Long id) {
        Optional<CorteDeCaja> corteOpt = corteCajaRepository.findById(id);
        if (corteOpt.isPresent()) {
            CorteDeCaja corte = corteOpt.get();
            List<Pedido> pedidosIncluidos = pedidoRepository.findByCorte(corte);
            return corteCajaService.generarResumenDesdePedidos(pedidosIncluidos);
        } else {
            return Collections.emptyList();
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarCorte(@PathVariable Long id) {
        if (corteCajaRepository.existsById(id)) {
            corteCajaRepository.deleteById(id);
            return ResponseEntity.ok("Corte eliminado con éxito.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Corte no encontrado.");
        }
    }
    @GetMapping("/descargar/pdf/{id}")
    public ResponseEntity<byte[]> descargarPDF(@PathVariable Long id) {
        byte[] pdfContent = corteCajaService.generarPDF(id);
        if (pdfContent != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=corte_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/descargar/excel/{id}")
    public ResponseEntity<byte[]> descargarExcel(@PathVariable Long id) {
        byte[] excelContent = corteCajaService.generarExcel(id);
        if (excelContent != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=corte_" + id + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelContent);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    private ResponseEntity<Resource> servirArchivo(String ruta, String tipoContenido) throws IOException {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource recurso = new InputStreamResource(new FileInputStream(archivo));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + archivo.getName())
                .contentLength(archivo.length())
                .contentType(MediaType.parseMediaType(tipoContenido))
                .body(recurso);
    }
    @PostMapping("/corte-de-caja/guardar-archivo")
    public ResponseEntity<String> guardarArchivo(@RequestParam("archivo") MultipartFile archivo) {
        try {
            ArchivoCorte nuevo = new ArchivoCorte();
            nuevo.setNombre(archivo.getOriginalFilename());
            nuevo.setTipo(archivo.getContentType());
            nuevo.setArchivo(archivo.getBytes());

            archivoCorteRepository.save(nuevo);
            return ResponseEntity.ok("Archivo guardado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo.");
        }
    }



}













