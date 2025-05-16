package com.delmonte.comedor_app.controllers;

import com.delmonte.comedor_app.models.Producto;
import com.delmonte.comedor_app.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductoController {

    // Carpeta donde se guardarán las imágenes
    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private ProductoRepository productoRepository;

    // Método para subir la imagen y retornar su URL
    @PostMapping("/imagen")
    public ResponseEntity<String> subirImagen(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(400).body("No se seleccionó ningún archivo.");
        }

        try {
            // Crear el nombre del archivo con su extensión
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            // Crear una ruta de destino
            Path targetLocation = Paths.get(uploadDir + File.separator + fileName);

            // Crear directorios si no existen
            Files.createDirectories(targetLocation.getParent());

            // Guardar la imagen en el directorio
            file.transferTo(targetLocation);

            // Retornar la URL relativa de la imagen
            String imageUrl = "/uploads/" + fileName;

            return ResponseEntity.ok(imageUrl); // Devolver la URL para usar en el frontend
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al subir la imagen.");
        }
    }

    // Crear un producto (POST) con la imagen
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestParam("nombre") String nombre,
                                                  @RequestParam("precio") Double precio,
                                                  @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            String imageUrl = null;

            // Subir la imagen si se incluye en la solicitud
            if (imagen != null && !imagen.isEmpty()) {
                // Subir la imagen y obtener la URL
                ResponseEntity<String> response = subirImagen(imagen);
                if (response.getStatusCodeValue() == 200) {
                    imageUrl = response.getBody(); // Obtener la URL desde el cuerpo de la respuesta
                } else {
                    return ResponseEntity.status(400).body(null); // Retornar error si la subida de imagen falla
                }
            }

            // Crear el producto
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setImagen(imageUrl);

            // Guardar el producto en la base de datos
            Producto productoGuardado = productoRepository.save(producto);

            return ResponseEntity.ok(productoGuardado); // Retornar el producto guardado

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // Obtener todos los productos (GET)
    @GetMapping
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    // Obtener un producto por ID (GET)
    @GetMapping("/{id}")
    public Producto obtenerProductoPorId(@PathVariable Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Actualizar un producto (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id,
                                                       @RequestParam("nombre") String nombre,
                                                       @RequestParam("precio") Double precio,
                                                       @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto == null) {
            return ResponseEntity.status(404).body(null); // No se encontró el producto
        }

        producto.setNombre(nombre);
        producto.setPrecio(precio);

        // Si hay una nueva imagen, se sube
        if (imagen != null && !imagen.isEmpty()) {
            ResponseEntity<String> response = subirImagen(imagen);
            if (response.getStatusCodeValue() == 200) {
                producto.setImagen(response.getBody()); // Actualizar la URL de la imagen
            } else {
                return ResponseEntity.status(400).body(null); // Si hay un error al subir la imagen
            }
        }

        Producto productoActualizado = productoRepository.save(producto);
        return ResponseEntity.ok(productoActualizado); // Devolver el producto actualizado
    }
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> obtenerProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }


    // Eliminar un producto (DELETE)
    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return "Producto eliminado con éxito.";
    }
}