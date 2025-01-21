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
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileManualPdf.InvoiceReconcileManualPdfRequest;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
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
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ReportPdfServiceImpl implements IReportPdfService {

    private final Logger log = LoggerFactory.getLogger(ReportPdfServiceImpl.class);
    private final ManageInvoiceServiceImpl invoiceService;
    private final IManageRoomRateService roomRateService;

    private static final float[] COLUMN_WIDTHS = {190F, 140F, 130F, 100F, 110F, 60F, 60F};
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public ReportPdfServiceImpl(ManageInvoiceServiceImpl invoiceService, IManageRoomRateService roomRateService) {

        this.invoiceService = invoiceService;
        this.roomRateService = roomRateService;
    }

    /*private byte[] generatePdf(ManageInvoiceDto invoiceDto) throws IOException { // Changed to IOException

        // Variables that are used in the creation of the pdf
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos); // Write to ByteArrayOutputStream
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Create a style with the desired font size
        Style styleHeader = new Style();
        styleHeader.setBorder(Border.NO_BORDER);
        styleHeader.setFontSize(10);

        Style styleCell = new Style();
        styleCell.setBorder(Border.NO_BORDER);
        styleCell.setFontSize(8);

        styleCell.setKeepTogether(false);
        String currencyData = invoiceDto.getHotel().getManageCurrency() != null ? invoiceDto.getHotel().getManageCurrency().getCode() : "USD";

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
        Paragraph hotelHead = new Paragraph(invoiceDto.getHotel() != null ? "Hotel: " + invoiceDto.getHotel().getCode() + "-" + invoiceDto.getHotel().getName() : "Hotel: ").addStyle(styleHeader);
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

//        for (ManageBookingDto booking : bookings) {
            // Add data row file 1
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell().add(new Paragraph((bookings.get(0).getHotelCreationDate()!=null ? bookings.get(0).getHotelCreationDate().format(formatter) : "Not date")).addStyle(styleCell)));
            table.addCell(new Cell().add(new Paragraph((bookings.get(0).getBookingDate()!=null ? bookings.get(0).getBookingDate().format(formatter) : "Not date")).addStyle(styleCell)));
            table.addCell(new Cell());
            table.addCell(new Cell());
//        }
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
            if (booking.getRoomType() != null || booking.getRatePlan() != null || booking.getRoomNumber() != null){
                table.addCell(new Cell().add(new Paragraph((booking.getRoomType() != null ? booking.getRoomType().getName() : "")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((booking.getRatePlan() != null ? booking.getRatePlan().getName() : "")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((booking.getRoomNumber() != null ? booking.getRoomNumber() : "")).addStyle(styleCell)));
                table.addCell(new Cell());
                table.addCell(new Cell());
                table.addCell(new Cell());
                table.addCell(new Cell());
            }
//            if (booking.getRoomType() != null) {
//                table.addCell(new Cell().add(new Paragraph((booking.getRoomType() != null ? booking.getRoomType().getName() : "")).addStyle(styleCell)));
//            } else {
//                table.addCell(new Cell().add(new Paragraph("").addStyle(styleCell)));
//            }
//            if (booking.getRoomType() != null) {
//                table.addCell(new Cell().add(new Paragraph((booking.getRatePlan() != null ? booking.getRatePlan().getName() : "")).addStyle(styleCell)));
//            } else {
//                table.addCell(new Cell().add(new Paragraph("").addStyle(styleCell)));
//            }
//            table.addCell(new Cell().add(new Paragraph((booking.getRoomNumber() != null ? booking.getRoomNumber() : "")).addStyle(styleCell)));
//            table.addCell(new Cell());
//            table.addCell(new Cell());
//            table.addCell(new Cell());
//            table.addCell(new Cell());
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
            List<ManageRoomRateDto> roomRates = this.roomRateService.findByBooking(booking.getId());
            for (ManageRoomRateDto roomRate : roomRates) {
                total = total + (roomRate.getInvoiceAmount() != null ? roomRate.getInvoiceAmount() : 0);
                moneyType = roomRate.getRemark() != null ? roomRate.getRemark() : "";

                table.addCell(new Cell().add(new Paragraph((roomRate.getCheckIn() != null ? roomRate.getCheckIn().format(formatter) : "Not date")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((roomRate.getCheckOut() != null ? roomRate.getCheckOut().format(formatter) : "Not date")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((roomRate.getNights() != null ? roomRate.getNights().toString() : "")).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getAdults() != null ? roomRate.getAdults().toString() : "").addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph(roomRate.getChildren() != null ? roomRate.getChildren().toString() : "").addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph("$ " + (roomRate.getInvoiceAmount() != null ? roomRate.getInvoiceAmount() : 0)).addStyle(styleCell)));
                table.addCell(new Cell().add(new Paragraph((currencyData)).addStyle(styleCell)));
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
        //table.addCell(new Cell().add(new Paragraph(!moneyType.isEmpty() ? moneyType : "USD" ).addStyle(styleCell)));
        table.addCell(new Cell().add(new Paragraph(currencyData).addStyle(styleCell)));

        document.add(table);
        document.close();

        return baos.toByteArray();
    }*/

    private byte[] generatePdf(ManageInvoiceDto invoiceDto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdfDoc);

        addTitle(document, "Invoice Data");
        Table table = createTable();

        addMainHeader(invoiceDto, table);
        addInternalHeadersAndRows(invoiceDto,  table);

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    private void addTitle(Document document, String titleText) {
        Paragraph title = new Paragraph(titleText)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
    }

    private Table createTable() {
        Table table = new Table(COLUMN_WIDTHS);
        table.setBorder(Border.NO_BORDER); // Sin bordes para toda la tabla
        return table;
    }

    private void addMainHeader(ManageInvoiceDto invoiceDto, Table table) {
        Style headerStyle = new Style().setFontSize(10).setBold();
        Style cellStyle = new Style().setFontSize(8);

        // Primera fila: Encabezado con la palabra "Hotel" y fechas
        table.addHeaderCell(new Cell()
                .add(new Paragraph("Hotel").addStyle(headerStyle))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)); // Solo "Hotel" como encabezado
        table.addHeaderCell(new Cell(1, 3).setBorder(Border.NO_BORDER)); // Espacio vacío
        table.addHeaderCell(new Cell().add(new Paragraph("Create Date").addStyle(headerStyle))
                .setBorder(Border.NO_BORDER));
        table.addHeaderCell(new Cell().setBorder(Border.NO_BORDER)); // Espacio vacío
        table.addHeaderCell(new Cell().add(new Paragraph("Booking Date").addStyle(headerStyle))
                .setBorder(Border.NO_BORDER));

        // Segunda fila: Nombre del hotel en una celda combinada de tres columnas
        String hotelName = (invoiceDto.getHotel() != null
                ? invoiceDto.getHotel().getCode() + " - " + invoiceDto.getHotel().getName()
                : "N/A");
        table.addCell(new Cell(1, 3)
                .add(new Paragraph(hotelName).addStyle(cellStyle))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)); // Sin borde
        table.addCell(new Cell().setBorder(Border.NO_BORDER)); // Celda vacía
        table.addCell(new Cell().add(new Paragraph(invoiceDto.getBookings().get(0).getHotelCreationDate() != null
                        ? invoiceDto.getBookings().get(0).getHotelCreationDate().format(FORMATTER)
                        : "Not date").addStyle(cellStyle))
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setBorder(Border.NO_BORDER)); // Celda vacía
        table.addCell(new Cell().add(new Paragraph(invoiceDto.getBookings().get(0).getBookingDate() != null
                        ? invoiceDto.getBookings().get(0).getBookingDate().format(FORMATTER)
                        : "Not date").addStyle(cellStyle))
                .setBorder(Border.NO_BORDER));
    }

    private void addInternalHeadersAndRows(ManageInvoiceDto invoiceDto, Table table) {
        List<ManageBookingDto> bookings = invoiceDto.getBookings();
        Style headerStyle = new Style().setFontSize(10).setBold();
        Style cellStyle = new Style().setFontSize(8);

        // Add "Voucher" header
        addInternalHeader(table, headerStyle, "Voucher", "Hotel Reservation", "Contract", "Night Type", "", "", "");

        for (ManageBookingDto booking : bookings) {
            table.addCell(createCell(booking.getCouponNumber(), cellStyle));
            table.addCell(createCell(booking.getHotelBookingNumber(), cellStyle));
            table.addCell(createCell(booking.getContract(), cellStyle));
            table.addCell(createCell(booking.getNightType() != null ? booking.getNightType().getName() : "", cellStyle));
            table.addCell(new Cell(1,3).setBorder(Border.NO_BORDER));
        }

        // Add "Room Type" header
        addInternalHeader(table, headerStyle, "Room Type", "Rate Plan", "Room Number", "", "", "", "");

        for (ManageBookingDto booking : bookings) {
            table.addCell(createCell(booking.getRoomType() != null ? booking.getRoomType().getName() : "", cellStyle));
            table.addCell(createCell(booking.getRatePlan() != null ? booking.getRatePlan().getName() : "", cellStyle));
            table.addCell(createCell(booking.getRoomNumber(), cellStyle));
            table.addCell(new Cell(1, 4).setBorder(Border.NO_BORDER));
        }

        // Add "Full Name" header
        addInternalHeader(table, headerStyle, "Full Name", "", "Remark", "", "", "", "");
        for (ManageBookingDto booking : bookings) {
            table.addCell(new Cell(1, 2) // "Full Name" ocupa 2 celdas
                    .add(new Paragraph(booking.getFullName() != null ? booking.getFullName() : "").addStyle(cellStyle))
                    .setBorder(Border.NO_BORDER));
            table.addCell(new Cell(1, 5) // "Remark" ocupa las 5 celdas restantes
                    .add(new Paragraph(booking.getHotelInvoiceNumber() != null ? booking.getHotelInvoiceNumber() : "").addStyle(cellStyle))
                    .setBorder(Border.NO_BORDER));
        }

        BigDecimal total = new BigDecimal(0);
        String currency = invoiceDto.getHotel().getManageCurrency() != null
                ? invoiceDto.getHotel().getManageCurrency().getCode()
                        : "USD";
        // Add "Check In" header
        addInternalHeader(table, headerStyle, "Check In", "Check Out", "Nights", "Adults", "Children", "Rate", "Currency");
        for (ManageBookingDto booking : bookings) {
            List<ManageRoomRateDto> roomRates = this.roomRateService.findByBooking(booking.getId());
            for (ManageRoomRateDto roomRate : roomRates) {
                BigDecimal invoiceAmount = new BigDecimal(roomRate.getInvoiceAmount() != null ? roomRate.getInvoiceAmount() : 0);
                total = total.add(invoiceAmount).setScale(2, RoundingMode.HALF_EVEN);

                table.addCell(createCell(roomRate.getCheckIn() != null ? roomRate.getCheckIn().format(FORMATTER) : "Not date", cellStyle));
                table.addCell(createCell(roomRate.getCheckOut() != null ? roomRate.getCheckOut().format(FORMATTER) : "Not date", cellStyle));
                table.addCell(createCell(roomRate.getNights() != null ? roomRate.getNights().toString() : "", cellStyle));
                table.addCell(createCell(roomRate.getAdults() != null ? roomRate.getAdults().toString() : "", cellStyle));
                table.addCell(createCell(roomRate.getChildren() != null ? roomRate.getChildren().toString() : "", cellStyle));
                table.addCell(createCell("$ " + (roomRate.getInvoiceAmount() != null ? roomRate.getInvoiceAmount() : 0), cellStyle));
                table.addCell(createCell(currency, cellStyle));
            }
        }

        addSummaryRows(total, currency, table);
    }

    private void addSummaryRows(BigDecimal total, String currency, Table table) {
        Style headerStyle = new Style().setFontSize(10).setBold();
        Style cellStyle = new Style().setFontSize(8);

        table.addCell(new Cell().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("TOTAL").addStyle(headerStyle)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("$ " + ScaleAmount.scaleAmount(total.doubleValue())).addStyle(cellStyle)).setBorder(Border.NO_BORDER));
        table.addCell(createCell(currency, cellStyle));
    }

    private void addInternalHeader(Table table, Style style, String... headers) {
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).addStyle(style)).setBorder(Border.NO_BORDER));
        }
    }

    private Cell createCell(String content, Style style) {
        return new Cell().add(new Paragraph(content != null ? content : "").addStyle(style)).setBorder(Border.NO_BORDER);
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
