package com.kynsoft.finamer.invoicing.infrastructure.services.report.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.kynsoft.finamer.invoicing.application.query.report.InvoiceMergeReportResponse;
import com.kynsoft.finamer.invoicing.application.query.report.InvoiceZipReportResponse;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportUtil {

    private ReportUtil() {
    }

    public static InvoiceMergeReportResponse createMergePaymentReportResponse(byte[] pdfContent, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
       if (Objects.nonNull(pdfContent)) {
           baos.write(pdfContent);
           baos.close();
           return new InvoiceMergeReportResponse(fileName, baos);
       }else{
           baos.write(defaultPdfContent());
           baos.close();
           return new InvoiceMergeReportResponse(fileName,baos);
       }
    }


    public static byte[] defaultPdfContent(){
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, data);
            document.open();
            document.addTitle("REPORT");
            document.addAuthor("FINAMER");
            Font chapterFont = new Font(Font.FontFamily.HELVETICA);
            Chunk chunk = new Chunk("NO REPORT TO SHOW", chapterFont);
            Chapter chapter = new Chapter(new Paragraph(chunk), 1);
            chapter.setNumberDepth(0);
            chunk.setBackground(BaseColor.GRAY);
            document.add(chapter);
            document.close();
            return data.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static InvoiceZipReportResponse createCompressResponse(Map<String,byte[]> pdfContent) throws IOException {
        File zipFile = File.createTempFile("files", ".zip");
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (Map.Entry<String, byte[]> mapEntry : pdfContent.entrySet()) {
                if (Objects.nonNull(mapEntry.getValue())) {
                    String filename = mapEntry.getKey()+".pdf";
                    byte[] fileContent = mapEntry.getValue();
                    ZipEntry zipEntry = new ZipEntry(filename);
                    zipOut.putNextEntry(zipEntry);
                    zipOut.write(fileContent, 0, fileContent.length);
                    zipOut.closeEntry();
                }
            }
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
       return new InvoiceZipReportResponse("invoices-files",resource);
    }
}
