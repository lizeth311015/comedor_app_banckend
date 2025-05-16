package com.delmonte.comedor_app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class CorteDeCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private double total;

    // **Nuevos campos para almacenar los archivos**
    @Lob
    @Column(name = "archivo_pdf")
    private byte[] archivoPdf;

    @Lob
    @Column(name = "archivo_excel")

    private byte[] archivoExcel;
    @OneToMany(mappedBy = "corte", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos;

    // Getters y setters existentes...

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }

    // —— Nuevos getters/setters para los archivos ——

    public byte[] getArchivoPdf() {
        return archivoPdf;
    }

    public void setArchivoPdf(byte[] archivoPdf) {
        this.archivoPdf = archivoPdf;
    }

    public byte[] getArchivoExcel() {
        return archivoExcel;
    }

    public void setArchivoExcel(byte[] archivoExcel) {
        this.archivoExcel = archivoExcel;
    }
}
