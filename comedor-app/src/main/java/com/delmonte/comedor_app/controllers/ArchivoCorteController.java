package com.delmonte.comedor_app.controllers;

import com.delmonte.comedor_app.models.ArchivoCorte;
import com.delmonte.comedor_app.repositories.ArchivoCorteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/corte-de-caja")
public class ArchivoCorteController {

    @Autowired
    private ArchivoCorteRepository archivoRepo;

    @PostMapping("/guardar-pdf")
    public ResponseEntity<String> guardarPDF(@RequestParam("file") MultipartFile file) {
        try {
            ArchivoCorte archivo = new ArchivoCorte();
            archivo.setNombre(file.getOriginalFilename());
            archivo.setTipo(file.getContentType());
            archivo.setArchivo(file.getBytes());

            archivoRepo.save(archivo);
            return ResponseEntity.ok("PDF guardado en base de datos correctamente.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo.");
        }
    }

    @GetMapping("/archivos")
    public ResponseEntity<?> listarArchivos() {
        return ResponseEntity.ok(archivoRepo.findAll());
    }
    @GetMapping("/archivo-descarga/{id}")
    public ResponseEntity<ByteArrayResource> descargarArchivo(@PathVariable Long id) {
        Optional<ArchivoCorte> archivoOpt = archivoRepo.findById(id);
        if (archivoOpt.isPresent()) {
            ArchivoCorte archivo = archivoOpt.get();
            ByteArrayResource resource = new ByteArrayResource(archivo.getArchivo());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + archivo.getNombre() + "\"")
                    .contentType(MediaType.parseMediaType(archivo.getTipo()))
                    .contentLength(archivo.getArchivo().length)
                    .body(resource);
        } else {
            // Aquí devolvemos un recurso vacío en lugar de un mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource(new byte[0]));  // Recurso vacío como respuesta
        }
    }




}


