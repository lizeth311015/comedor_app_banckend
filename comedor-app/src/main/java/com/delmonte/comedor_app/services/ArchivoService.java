package com.delmonte.comedor_app.services;

import com.delmonte.comedor_app.dto.ResumenEmpleadoDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArchivoService {

    public byte[] generarPDFResumen(List<ResumenEmpleadoDTO> resumenClientes, double totalGeneral) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Resumen de Corte de Caja", titleFont));
            document.add(new Paragraph("Fecha: " + LocalDateTime.now(), normalFont));
            document.add(new Paragraph("Total General: $" + totalGeneral, normalFont));
            document.add(Chunk.NEWLINE);

            for (ResumenEmpleadoDTO resumen : resumenClientes) {
                document.add(new Paragraph("Cliente: " + resumen.getNombreCliente(), normalFont));
                document.add(new Paragraph("Clave: " + resumen.getClaveEmpleado(), normalFont));
                document.add(new Paragraph("Total gastado: $" + resumen.getTotalGastado(), normalFont));

                if (resumen.getProductosConsumidos() != null) {
                    for (String producto : resumen.getProductosConsumidos()) {
                        document.add(new Paragraph("    • " + producto, normalFont));
                    }
                }

                document.add(Chunk.NEWLINE);
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generarExcelResumen(List<ResumenEmpleadoDTO> resumenClientes, double totalGeneral) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Resumen Corte");

            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Clave");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Total Gastado");
            header.createCell(3).setCellValue("Productos Consumidos");

            for (ResumenEmpleadoDTO resumen : resumenClientes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(resumen.getClaveEmpleado());
                row.createCell(1).setCellValue(resumen.getNombreCliente());
                row.createCell(2).setCellValue(resumen.getTotalGastado());
                row.createCell(3).setCellValue(
                        resumen.getProductosConsumidos() != null ?
                                String.join(", ", resumen.getProductosConsumidos()) :
                                ""
                );
            }

            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.createCell(1).setCellValue("TOTAL GENERAL:");
            totalRow.createCell(2).setCellValue(totalGeneral);

            for (int i = 0; i <= 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
