package com.delmonte.comedor_app.services;

import com.delmonte.comedor_app.dto.ResumenEmpleadoDTO;
import com.delmonte.comedor_app.models.Cliente;
import com.delmonte.comedor_app.models.CorteDeCaja;
import com.delmonte.comedor_app.models.Pedido;
import com.delmonte.comedor_app.repositories.ClienteRepository;
import com.delmonte.comedor_app.repositories.CorteDeCajaRepository;
import com.delmonte.comedor_app.repositories.PedidoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorteCajaService {

    @Autowired
    private CorteDeCajaRepository corteCajaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;


    @Autowired
    private ArchivoService archivoService;



    public List<ResumenEmpleadoDTO> generarResumenYEliminarPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        Map<String, ResumenEmpleadoDTO> resumenMap = new HashMap<>();

        for (Pedido pedido : pedidos) {
            if (pedido.getProducto() != null && pedido.getProducto().getNombre() != null) {
                String clave = pedido.getCliente().getClaveEmpleado();
                String nombre = pedido.getCliente().getNombre();
                Double total = pedido.getTotal();
                String nombreProducto = pedido.getProducto().getNombre();

                // Crear el resumen si no existe aún
                resumenMap.putIfAbsent(clave, new ResumenEmpleadoDTO());
                ResumenEmpleadoDTO resumen = resumenMap.get(clave);

                // ← ESTOS CAMPOS ESTABAN FALTANDO
                resumen.setClaveEmpleado(clave);
                resumen.setNombreCliente(nombre);

                resumen.setTotalGastado(resumen.getTotalGastado() + total);
                resumen.getProductosConsumidos().add(nombreProducto);
            }
        }

        // Eliminar pedidos y resetear saldo como ya hacías
        pedidoRepository.deleteAll();

        for (String clave : resumenMap.keySet()) {
            Cliente cliente = clienteRepository.findByClaveEmpleado(clave);
            if (cliente != null) {
                cliente.setSaldo(0.0);
                clienteRepository.save(cliente);
            }
        }

        return new ArrayList<>(resumenMap.values());
    }



    public CorteDeCaja guardarCorteActual(List<Pedido> pedidosNoAsignados) {
        // 1) Crear el objeto CorteDeCaja
        CorteDeCaja nuevoCorte = new CorteDeCaja();
        nuevoCorte.setFecha(LocalDateTime.now());
        // (si quieres, calcula y setea total aquí)

        // 2) Guardar el corte para que obtenga un ID
        nuevoCorte = corteCajaRepository.save(nuevoCorte);

        // 3) Asociar todos los pedidos al corte ya persistido
        for (Pedido pedido : pedidosNoAsignados) {
            pedido.setCorte(nuevoCorte);
        }
        // 4) Guardar todos los pedidos en bloque
        pedidoRepository.saveAll(pedidosNoAsignados);

        // 5) Generar y guardar archivos, etc.
        List<ResumenEmpleadoDTO> resumen = generarResumenDesdePedidos(pedidosNoAsignados);
        generarArchivos(nuevoCorte, resumen);

        return nuevoCorte;
    }


    private void asegurarCarpetaDocumentos() {
        File carpeta = new File("documentos/");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    public void generarArchivos(CorteDeCaja corte, List<ResumenEmpleadoDTO> resumen) {
        try {
            // Aseguramos que la carpeta exista
            File carpeta = new File("documentos");
            if (!carpeta.exists()) carpeta.mkdirs();

            // ================== PDF ==================
            String pdfPath = "documentos/corte_" + corte.getId() + ".pdf";
            Document pdf = new Document();
            PdfWriter.getInstance(pdf, new FileOutputStream(pdfPath));
            pdf.open();
            pdf.add(new Paragraph("Corte de Caja - ID: " + corte.getId()));
            pdf.add(new Paragraph("Fecha: " + corte.getFecha()));
            pdf.add(new Paragraph("Total General: $" + corte.getTotal()));
            pdf.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(3);
            pdfTable.addCell("Clave Empleado");
            pdfTable.addCell("Nombre");
            pdfTable.addCell("Total Gastado");

            for (ResumenEmpleadoDTO dto : resumen) {
                pdfTable.addCell(dto.getClaveEmpleado());
                pdfTable.addCell(dto.getNombreCliente());
                pdfTable.addCell("$" + dto.getTotalGastado());
            }

            pdf.add(pdfTable);
            pdf.close();

            // ================== EXCEL ==================
            String excelPath = "documentos/corte_" + corte.getId() + ".xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Corte de Caja");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Clave Empleado");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Total Gastado");

            int rowNum = 1;
            for (ResumenEmpleadoDTO dto : resumen) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dto.getClaveEmpleado());
                row.createCell(1).setCellValue(dto.getNombreCliente());
                row.createCell(2).setCellValue(dto.getTotalGastado());
            }

            FileOutputStream fileOut = new FileOutputStream(excelPath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            // Aquí puedes lanzar una excepción o simplemente registrar el error
        }
    }

    public List<CorteDeCaja> obtenerCortesGuardados() {
        return corteCajaRepository.findAll();
    }

    public boolean eliminarCortePorId(Long id) {
        if (corteCajaRepository.existsById(id)) {
            corteCajaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void generarPDF(CorteDeCaja corte, List<ResumenEmpleadoDTO> resumen) throws Exception {
        Document document = new Document();
        String filePath = "documentos/corte_" + corte.getId() + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        document.add(new Paragraph("Corte de Caja - ID: " + corte.getId()));
        document.add(new Paragraph("Fecha: " + corte.getFecha()));
        document.add(new Paragraph("Total: $" + corte.getTotal()));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.addCell("Clave");
        table.addCell("Nombre");
        table.addCell("Total Gastado");
        table.addCell("Productos");

        for (ResumenEmpleadoDTO dto : resumen) {
            table.addCell(dto.getClaveEmpleado());
            table.addCell(dto.getNombreCliente());
            table.addCell(String.format("$%.2f", dto.getTotalGastado()));
            table.addCell(String.join(", ", dto.getProductosConsumidos()));
        }

        document.add(table);
        document.close();
    }

    private void generarExcel(CorteDeCaja corte, List<ResumenEmpleadoDTO> resumen) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Corte_" + corte.getId());

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Clave");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Total Gastado");
        header.createCell(3).setCellValue("Productos");

        int i = 1;
        for (ResumenEmpleadoDTO dto : resumen) {
            Row row = sheet.createRow(i++);
            row.createCell(0).setCellValue(dto.getClaveEmpleado());
            row.createCell(1).setCellValue(dto.getNombreCliente());
            row.createCell(2).setCellValue(dto.getTotalGastado());
            row.createCell(3).setCellValue(String.join(", ", dto.getProductosConsumidos()));
        }

        FileOutputStream out = new FileOutputStream("documentos/corte_" + corte.getId() + ".xlsx");
        workbook.write(out);
        workbook.close();
        out.close();
    }

    public List<ResumenEmpleadoDTO> generarResumenDesdePedidos(List<Pedido> pedidos) {
        Map<String, ResumenEmpleadoDTO> resumenMap = new HashMap<>();

        for (Pedido pedido : pedidos) {
            Cliente cliente = pedido.getCliente();
            String clave = cliente.getClaveEmpleado();
            String nombre = cliente.getNombre();
            double totalPedido = pedido.getTotal();
            String nombreProducto = pedido.getProducto().getNombre();

            resumenMap.putIfAbsent(clave, new ResumenEmpleadoDTO());
            ResumenEmpleadoDTO dto = resumenMap.get(clave);
            dto.setTotalGastado(dto.getTotalGastado() + totalPedido);
            dto.getProductosConsumidos().add(nombreProducto);
        }

        return new ArrayList<>(resumenMap.values());
    }

    public void generarYGuardarCorte() {
        // 1. Obtener pedidos que no están en ningún corte (idCorte == null)
        List<Pedido> pedidosSinCorte = pedidoRepository.findByCorteIsNull();
        if (pedidosSinCorte.isEmpty()) return;

        // 2. Agrupar pedidos por cliente
        Map<String, List<Pedido>> pedidosPorCliente = pedidosSinCorte.stream()
                .collect(Collectors.groupingBy(Pedido::getClaveEmpleado));

        List<ResumenEmpleadoDTO> resumenes = new ArrayList<>();
        double totalGeneral = 0.0;

        for (Map.Entry<String, List<Pedido>> entry : pedidosPorCliente.entrySet()) {
            List<Pedido> pedidosCliente = entry.getValue();
            if (pedidosCliente.isEmpty()) continue;

            Pedido p = pedidosCliente.get(0); // Todos tienen misma clave y cliente
            Cliente cliente = p.getCliente();
            double total = pedidosCliente.stream().mapToDouble(Pedido::getTotal).sum();
            totalGeneral += total;

            List<String> productos = pedidosCliente.stream()
                    .map(pedido -> pedido.getProducto().getNombre())
                    .collect(Collectors.toList());

            resumenes.add(new ResumenEmpleadoDTO(
                    cliente.getClaveEmpleado(),
                    cliente.getNombre(),
                    total,
                    productos
            ));
        }

        // 3. Generar archivos PDF y Excel
        byte[] pdfBytes = archivoService.generarPDFResumen(resumenes, totalGeneral);
        byte[] excelBytes = archivoService.generarExcelResumen(resumenes, totalGeneral);

// ...
// 4. Guardar corte con archivos
        CorteDeCaja corte = new CorteDeCaja();
        corte.setFecha(LocalDateTime.now());
        corte.setTotal(totalGeneral);
        corte.setArchivoPdf(pdfBytes);
        corte.setArchivoExcel(excelBytes);
        corteCajaRepository.save(corte);

// 5. Asociar el corte a cada pedido
        for (Pedido pedido : pedidosSinCorte) {
            pedido.setCorte(corte);
        }
        pedidoRepository.saveAll(pedidosSinCorte);

    }

    public byte[] generarPDF(Long id) {
        Optional<CorteDeCaja> optional = corteCajaRepository.findById(id);
        if (optional.isPresent()) {
            CorteDeCaja corte = optional.get();
            return corte.getArchivoPdf();
        }
        return null;
    }

    public byte[] generarExcel(Long id) {
        Optional<CorteDeCaja> optional = corteCajaRepository.findById(id);
        if (optional.isPresent()) {
            CorteDeCaja corte = optional.get();
            return corte.getArchivoExcel();
        }
        return null;
    }


}

