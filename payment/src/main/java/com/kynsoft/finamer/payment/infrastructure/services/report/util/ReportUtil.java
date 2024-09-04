package com.kynsoft.finamer.payment.infrastructure.services.report.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.kynsoft.finamer.payment.application.query.report.PaymentReportResponse;
import org.springframework.security.core.parameters.P;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ReportUtil {

    private ReportUtil() {
    }

    public static PaymentReportResponse createPaymentReportResponse(byte[] pdfContent, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
       if (Objects.nonNull(pdfContent)) {
           baos.write(pdfContent);
           return new PaymentReportResponse(fileName, baos);
       }else{
           baos.write(defaultPdfContent());
           return new PaymentReportResponse(fileName,baos);
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
}
