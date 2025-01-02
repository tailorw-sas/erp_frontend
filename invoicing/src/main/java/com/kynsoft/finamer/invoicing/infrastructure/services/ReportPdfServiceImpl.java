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
import com.itextpdf.text.Font;
import com.kynsof.share.utils.ScaleAmount;
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
        String currencyData = invoiceDto.getHotel().getManageCurrency().getCode();

        List<ManageBookingDto> bookings = invoiceDto.getBookings();

        double total = 0;
        String moneyType = "$";
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        //Add pdf title
        Paragraph title = new Paragraph("Invoice Data");
        title.setBold();
        title.setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Create table whit 7 columns
        float[] columnWidths = {190F, 140F, 130F, 100F, 110F, 60F, 60F};
        Table table = new Table(columnWidths);
        table.setBorder(Border.NO_BORDER);

        // Add header row
        Paragraph hotelHead = new Paragraph("Hotel: " + invoiceDto.getHotel() != null ? invoiceDto.getHotel().getCode() + "-" + invoiceDto.getHotel().getName() : "").addStyle(styleHeader);
        hotelHead.setBold();
        table.addHeaderCell(new Cell().add(hotelHead));
        table.addHeaderCell(new Cell());
        table.addHeaderCell(new Cell());

        Paragraph cDateHead = new Paragraph("Create Date").addStyle(styleHeader);
        cDateHead.setBold();
        table.addHeaderCell(new Cell().add(cDateHead));

        Paragraph bookingDate = new Paragraph("Booking Date").addStyle(styleHeader);
        bookingDate.setBold();
        table.addHeaderCell(new Cell().add(bookingDate));

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
        Paragraph voucher = new Paragraph("Voucher").addStyle(styleHeader);
        voucher.setBold();
        table.addCell(new Cell().add(voucher));

        Paragraph hotelReservation = new Paragraph("Hotel Reservation").addStyle(styleHeader);
        hotelReservation.setBold();
        table.addCell(new Cell().add(hotelReservation));

        Paragraph contract = new Paragraph("Contract").addStyle(styleHeader);
        contract.setBold();
        table.addCell(new Cell().add(contract));

        Paragraph nigtType = new Paragraph("Night Type").addStyle(styleHeader);
        nigtType.setBold();
        table.addCell(new Cell().add(nigtType));

        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Add data row file 3
        for (ManageBookingDto booking : bookings) {
            table.addCell(new Cell().add(new Paragraph((booking.getCouponNumber()!= null ? booking.getCouponNumber() : "")).addStyle(styleCell)));
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
        Paragraph roomType = new Paragraph("Room Type").addStyle(styleHeader);
        roomType.setBold();
        table.addCell(new Cell().add(roomType));

        Paragraph ratePlan = new Paragraph("Rate Plan").addStyle(styleHeader);
        ratePlan.setBold();
        table.addCell(new Cell().add(ratePlan));

        Paragraph roomNumber = new Paragraph("Room Number").addStyle(styleHeader);
        roomNumber.setBold();
        table.addCell(new Cell().add(roomNumber));

        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Add data row file 5
        for (ManageBookingDto booking : bookings) {
            if (booking.getRoomType() != null) {
                table.addCell(new Cell().add(new Paragraph((booking.getRoomType() != null ? booking.getRoomType().getName() : "")).addStyle(styleCell)));
            } else {
                table.addCell(new Cell().add(new Paragraph("").addStyle(styleCell)));
            }
            if (booking.getRoomType() != null) {
                table.addCell(new Cell().add(new Paragraph((booking.getRatePlan() != null ? booking.getRatePlan().getName() : "")).addStyle(styleCell)));
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
        Paragraph fullName = new Paragraph("Full Name").addStyle(styleHeader);
        fullName.setBold();
        table.addCell(new Cell().add(fullName));

        table.addCell(new Cell());

        Paragraph remark = new Paragraph("Remark").addStyle(styleHeader);
        remark.setBold();
        table.addCell(new Cell().add(remark));

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
        Paragraph checkin = new Paragraph("Check In").addStyle(styleHeader);
        checkin.setBold();
        table.addCell(new Cell().add(checkin));

        Paragraph checkout = new Paragraph("Check Out").addStyle(styleHeader);
        checkout.setBold();
        table.addCell(new Cell().add(checkout));

        Paragraph nigths = new Paragraph("Nights").addStyle(styleHeader);
        nigths.setBold();
        table.addCell(new Cell().add(nigths));

        Paragraph adults = new Paragraph("Adults").addStyle(styleHeader);
        adults.setBold();
        table.addCell(new Cell().add(adults));

        Paragraph children = new Paragraph("Children").addStyle(styleHeader);
        children.setBold();
        table.addCell(new Cell().add(children));

        Paragraph rate = new Paragraph("Rate").addStyle(styleHeader);
        rate.setBold();
        table.addCell(new Cell().add(rate));

        Paragraph currency = new Paragraph("Currency").addStyle(styleHeader);
        currency.setBold();
        table.addCell(new Cell().add(currency));

        // Add data row file 9
        for (ManageBookingDto booking : bookings) {
            List<ManageRoomRateDto> roomRates = booking.getRoomRates();
            for (ManageRoomRateDto roomRate : roomRates) {
                total = total + (roomRate.getHotelAmount() != null ? roomRate.getHotelAmount() : 0);
                moneyType = roomRate.getRemark() != null ? roomRate.getRemark() : "";

                table.addCell(new Cell().add(new Paragraph((roomRate.getCheckIn() != null ? roomRate.getCheckIn().format(formatter) : "Not date")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((roomRate.getCheckOut() != null ? roomRate.getCheckOut().format(formatter) : "Not date")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((roomRate.getNights() != null ? roomRate.getNights().toString() : "")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getAdults() != null ? roomRate.getAdults().toString() : "").addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getChildren() != null ? roomRate.getChildren().toString() : "").addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph("$ " + (roomRate.getInvoiceAmount() != null ? roomRate.getInvoiceAmount() : 0)).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((currencyData != null ? currencyData : "$")).addStyle(styleCell)));
            }
        }
        // Add data row file 10
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        Paragraph totalP = new Paragraph("TOTAL").addStyle(styleHeader);
        totalP.setBold();
        table.addCell(new Cell().add(totalP));
        table.addCell(new Cell());

        // Add data row file 11
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell().add(new Paragraph("$ " + ScaleAmount.scaleAmount(total)).addStyle(styleCell)));
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
