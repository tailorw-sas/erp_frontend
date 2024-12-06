package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileManualPdf.InvoiceReconcileManualPdfRequest;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IReportPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ReportPdfServiceImpl implements IReportPdfService {

    private final Logger log = LoggerFactory.getLogger(ReportPdfServiceImpl.class);
    private final ManageInvoiceServiceImpl invoiceService;

    public ReportPdfServiceImpl(ManageInvoiceServiceImpl invoiceService) {

        this.invoiceService = invoiceService;
    }

    private byte[] generatePdf(ManageInvoiceDto invoiceDto) throws IOException { // Changed to IOException

        // Variables that are used in the creation of the pdf
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos); // Write to ByteArrayOutputStream
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Create a style with the desired font size
        Style styleHeader = new Style();
        styleHeader.setFontSize(10);
        Style styleCell = new Style();
        styleCell.setFontSize(8);

        List<ManageBookingDto> bookings = invoiceDto.getBookings();

        double total = 0;
        String moneyType = "$";

        //Add pdf title
        Paragraph title = new Paragraph("Invoicing Data");
        title.setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Create table whit 7 columns
        float[] columnWidths = {190F, 140F, 130F, 100F, 110F, 60F, 60F};
        Table table = new Table(columnWidths);
        table.setBorder(Border.NO_BORDER);

        // Add header row
        table.addHeaderCell(new Cell().add(new Paragraph("Hotel: " + invoiceDto.getHotel().getName()).addStyle(styleHeader)));
        table.addHeaderCell(new Cell());
        table.addHeaderCell(new Cell());
        table.addHeaderCell(new Cell().add(new Paragraph("Create Date").addStyle(styleHeader)));
        table.addHeaderCell(new Cell().add(new Paragraph("Booking Date").addStyle(styleHeader)));
        table.addHeaderCell(new Cell());
        table.addHeaderCell(new Cell());

        for (ManageBookingDto booking : bookings) {
            // Add data row file 1
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell().add(new Paragraph((booking.getHotelCreationDate()!=null ? booking.getHotelCreationDate().format(formatter) : "Not date")).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph((booking.getBookingDate()!=null ? booking.getBookingDate().format(formatter) : "Not date")).addStyle(styleCell)));
            table.addCell(new Cell());
            table.addCell(new Cell());
        }
        //2 empty columns
        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().setMinHeight(10));
        }

        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().setMinHeight(10));
        }

        // Add header row file 2
        table.addCell(new Cell().add(new Paragraph("Voucher").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Hotel Reservation").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Contract").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Night Type").addStyle(styleHeader)));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Add data row file 3
        for (ManageBookingDto booking : bookings) {
            table.addCell(new Cell().add(new Paragraph((booking.getCouponNumber()!=null ? booking.getCouponNumber() : "")).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph((booking.getHotelBookingNumber() != null ? booking.getHotelBookingNumber() : "")).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph((booking.getContract() != null ? booking.getContract() : "")).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph((booking.getNightType() != null ? booking.getNightType().getName() : "")).addStyle(styleCell)));
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
        }

        //2 empty columns
        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().setMinHeight(10));
        }

        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().setMinHeight(10));
        }

        // Add data row file 4
        table.addCell(new Cell().add(new Paragraph("Room Type").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Rate Plan").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Room Number").addStyle(styleHeader)));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Add data row file 5
        for (ManageBookingDto booking : bookings) {
            if (booking.getRoomType() != null) {
                table.addCell(new Cell().add(new Paragraph((booking.getRoomType().getName() != null ? booking.getRoomType().getName() : "")).addStyle(styleCell)));
            } else {
                table.addCell(new Cell().add(new Paragraph("").addStyle(styleCell)));
            }
            if (booking.getRoomType() != null) {
                table.addCell(new Cell().add(new Paragraph((booking.getRatePlan().getName() != null ? booking.getRatePlan().getName() : "")).addStyle(styleCell)));
            } else {
                table.addCell(new Cell().add(new Paragraph("").addStyle(styleCell)));
            }
            table.addCell(new Cell().add(new Paragraph((booking.getRoomNumber() != null ? booking.getRoomNumber() : "")).addStyle(styleCell)));
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
        }

        //2 empty columns
        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().setMinHeight(10));
        }

        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().setMinHeight(10));
        }

        // Add data row file 6
        table.addCell(new Cell().add(new Paragraph("Full Name").addStyle(styleHeader)));
        table.addCell(new Cell());
        table.addCell(new Cell().add(new Paragraph("Remark").addStyle(styleHeader)));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Add data row file 7
        for (ManageBookingDto booking : bookings) {
            table.addCell(new Cell().add(new Paragraph((booking.getFullName() != null ? booking.getFullName() : "")).addStyle(styleCell)));
            table.addCell(new Cell());
            table.addCell(new Cell().add(new Paragraph((booking.getHotelInvoiceNumber() != null ? booking.getHotelInvoiceNumber() : "")).addStyle(styleCell)));
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
        }

        //2 empty columns
        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().setMinHeight(10));
        }

        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().setMinHeight(10));
        }

        // Add data row file 8
        table.addCell(new Cell().add(new Paragraph("Check In").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Check Out").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Nights").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Adults").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Children").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Rate").addStyle(styleHeader)));
        table.addCell(new Cell().add(new Paragraph("Currency").addStyle(styleHeader)));

        // Add data row file 9
        for (ManageBookingDto booking : bookings) {
            List<ManageRoomRateDto> roomRates = booking.getRoomRates();
            for (ManageRoomRateDto roomRate : roomRates) {
                total = total + (roomRate.getHotelAmount() != null ? roomRate.getHotelAmount() : 0);
                moneyType = roomRate.getRemark();

                table.addCell(new Cell().add(new Paragraph((roomRate.getCheckIn() != null ? roomRate.getCheckIn().format(formatter) : "Not date")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((roomRate.getCheckOut() != null ? roomRate.getCheckOut().format(formatter) : "Not date")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((roomRate.getNights() != null ? roomRate.getNights().toString() : "")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getAdults() != null ? roomRate.getAdults().toString() : "").addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getChildren() != null ? roomRate.getChildren().toString() : "").addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph("$ " + (roomRate.getHotelAmount() != null ? roomRate.getHotelAmount() : 0)).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((roomRate.getRemark() != null && !roomRate.getRemark().isEmpty()  ? roomRate.getRemark() : "$")).addStyle(styleCell)));
            }
        }
        // Add data row file 10
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell().add(new Paragraph("TOTAL").addStyle(styleHeader)));
        table.addCell(new Cell());

        // Add data row file 11
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell().add(new Paragraph("$ " + total).addStyle(styleCell)));
        table.addCell(new Cell().add(new Paragraph(!moneyType.isEmpty() ? moneyType : "$" ).addStyle(styleCell)));

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    // Método para combinar PDFs generados desde una lista de IDs
    @Override
    public byte[] concatenatePDFs(String[] ids) throws IOException {
        // Crear un PdfDocument de salida
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedPdf = new PdfDocument(new PdfWriter(outputStream));

        for (String id : ids) {
            ManageInvoiceDto invoiceDto = invoiceService.findById(UUID.fromString(id));
            // Generar el PDF para el INVOICEDTO actual
            byte[] pdfBytes = generatePdf((invoiceDto));

            // Leer el PDF desde los bytes generados
            PdfDocument sourcePdf = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)));

            // Copiar páginas al PDF combinado
            sourcePdf.copyPagesTo(1, sourcePdf.getNumberOfPages(), mergedPdf);
            sourcePdf.close();
        }

        // Cerrar el PDF combinado
        mergedPdf.close();

        return outputStream.toByteArray(); // Devuelve el PDF combinado como un array de bytes
    }

    // Método para combinar PDFs generados desde una lista de UUIDs
    @Override
    public byte[] concatenateManualPDFs(InvoiceReconcileManualPdfRequest request) throws IOException {
        // Crear un PdfDocument de salida
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedPdf = new PdfDocument(new PdfWriter(outputStream));
        try {
            for (UUID invoice : request.getInvoices()) {
                ManageInvoiceDto invoiceDto = invoiceService.findById(invoice);
                // Generar el PDF para el INVOICING ID  actual
                byte[] pdfBytes = generatePdf(invoiceDto);

                // Leer el PDF desde los bytes generados
                PdfDocument sourcePdf = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)));

                // Copiar páginas al PDF combinado
                sourcePdf.copyPagesTo(1, sourcePdf.getNumberOfPages(), mergedPdf);
                sourcePdf.close();
            }
        } catch (IOException | PdfException e) {
            // Maneja la excepción apropiadamente
            throw new RuntimeException("Error procesando el PDF", e);
        }

        // Cerrar el PDF combinado
        mergedPdf.close();

        return outputStream.toByteArray(); // Devuelve el PDF combinado como un array de bytes
    }
}
