package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ReportPdfServiceImpl {

    private final Logger log = LoggerFactory.getLogger(ReportPdfServiceImpl.class);
    private final ManageInvoiceServiceImpl invoiceService;
    private final ManageBookingServiceImpl bookingService;
    public ReportPdfServiceImpl(ManageInvoiceServiceImpl invoiceService,
                                ManageBookingServiceImpl bookingService){

        this.invoiceService = invoiceService;
        this.bookingService = bookingService;

    }
    public ManageInvoiceDto findById(UUID id) {
        ManageInvoiceDto invoiceDto = invoiceService.findById(id);
        List<ManageBookingDto> bookings= bookingService.findByManageInvoicing(id);
        invoiceDto.setBookings(bookings);
        return invoiceDto;

    }
    public byte[] generatePdf(UUID id) throws IOException { // Changed to IOException

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
        ManageInvoiceDto invoiceDto = this.findById(id);
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

        for(ManageBookingDto booking : bookings) {
            // Add data row file 1
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell().add(new Paragraph(booking.getHotelCreationDate().format(formatter)).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph(booking.getBookingDate().format(formatter)).addStyle(styleCell)));
            table.addCell(new Cell());
            table.addCell(new Cell());
        }
        //2 empty columns
        for(int i = 0; i<7;i++){
            table.addCell(new Cell().setMinHeight(10));
        }

        for(int i = 0; i<7;i++){
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
        for(ManageBookingDto booking : bookings) {
            table.addCell(new Cell().add(new Paragraph(booking.getCouponNumber()).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph(booking.getHotelBookingNumber()).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph(booking.getContract()).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph(booking.getNightType().getName()).addStyle(styleCell)));
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
        }
        //2 empty columns
        for(int i = 0; i<7;i++){
            table.addCell(new Cell().setMinHeight(10));
        }

        for(int i = 0; i<7;i++){
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
        for(ManageBookingDto booking : bookings) {
        table.addCell(new Cell().add(new Paragraph(booking.getRoomType().getName()).addStyle(styleCell)));
        table.addCell(new Cell().add(new Paragraph(booking.getRatePlan().getName()).addStyle(styleCell)));
        table.addCell(new Cell().add(new Paragraph(booking.getRoomNumber()).addStyle(styleCell)));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        }

        //2 empty columns
        for(int i = 0; i<7;i++){
            table.addCell(new Cell().setMinHeight(10));
        }

        for(int i = 0; i<7;i++){
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
        for(ManageBookingDto booking : bookings) {
        table.addCell(new Cell().add(new Paragraph(booking.getFullName()).addStyle(styleCell)));
        table.addCell(new Cell());
        table.addCell(new Cell().add(new Paragraph(booking.getHotelInvoiceNumber()).addStyle(styleCell)));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());
        }

        //2 empty columns
        for(int i = 0; i<7;i++){
            table.addCell(new Cell().setMinHeight(10));
        }

        for(int i = 0; i<7;i++){
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
        for(ManageBookingDto booking : bookings) {
            List<ManageRoomRateDto> roomRates = booking.getRoomRates();
            for (ManageRoomRateDto roomRate : roomRates) {
                total = total + roomRate.getInvoiceAmount();
                moneyType = roomRate.getRemark();

                table.addCell(new Cell().add(new Paragraph(roomRate.getCheckIn().format(formatter)).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getCheckOut().format(formatter)).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getNights().toString()).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getAdults().toString()).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getChildren().toString()).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph("$ " + roomRate.getInvoiceAmount().toString()).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getRemark()).addStyle(styleCell)));
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
        table.addCell(new Cell().add(new Paragraph("$ "+ total).addStyle(styleCell)));
        table.addCell(new Cell().add(new Paragraph(moneyType).addStyle(styleCell)));

        document.add(table);
        document.close();

        return baos.toByteArray();
    }
}