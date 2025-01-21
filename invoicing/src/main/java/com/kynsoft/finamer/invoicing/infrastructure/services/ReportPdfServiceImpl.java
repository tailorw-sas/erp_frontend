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

    private final ManageInvoiceServiceImpl invoiceService;
    private final IManageRoomRateService roomRateService;

    private static final float[] COLUMN_WIDTHS = {140F, 140F, 130F, 100F, 110F, 60F, 110F};
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Style headerStyle = new Style().setFontSize(10).setBold();
    private static final Style cellStyle = new Style().setFontSize(8);

    public ReportPdfServiceImpl(ManageInvoiceServiceImpl invoiceService, IManageRoomRateService roomRateService) {
        this.invoiceService = invoiceService;
        this.roomRateService = roomRateService;
    }

    private byte[] generatePdf(ManageInvoiceDto invoiceDto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdfDoc);

        addTitle(document);
        Table table = createTable();

        addMainHeader(invoiceDto, table);
        addInternalHeadersAndRows(invoiceDto,  table);

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    private void addTitle(Document document) {
        Paragraph title = new Paragraph("Invoice Data")
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
        // Primera fila: Encabezado con la palabra "Hotel" y fechas
        table.addHeaderCell(createCell("Hotel",headerStyle).setTextAlignment(TextAlignment.LEFT)); // Solo "Hotel" como encabezado
        table.addHeaderCell(createEmptyCell(3)); // Espacio vacío
        table.addHeaderCell(createCell("Create Date",headerStyle));
        table.addHeaderCell(createEmptyCell()); // Espacio vacío
        table.addHeaderCell(createCell("Booking Date",headerStyle));

        // Segunda fila: Nombre del hotel en una celda combinada de tres columnas
        String hotelName = (invoiceDto.getHotel() != null
                ? invoiceDto.getHotel().getCode() + " - " + invoiceDto.getHotel().getName()
                : "N/A");
        String hotelCreationDate =  invoiceDto.getBookings().get(0).getHotelCreationDate() != null
                ? invoiceDto.getBookings().get(0).getHotelCreationDate().format(FORMATTER)
                : "Not date";
        String bookingDate = invoiceDto.getBookings().get(0).getBookingDate() != null
                ? invoiceDto.getBookings().get(0).getBookingDate().format(FORMATTER)
                : "Not date";

        table.addCell(createCell(3, hotelName).setTextAlignment(TextAlignment.LEFT));
        table.addCell(createEmptyCell()); // Celda vacía
        table.addCell(createCell(hotelCreationDate, cellStyle));
        table.addCell(createEmptyCell()); // Celda vacía
        table.addCell(createCell(bookingDate , cellStyle));
    }

    private void addInternalHeadersAndRows(ManageInvoiceDto invoiceDto, Table table) {
        List<ManageBookingDto> bookings = invoiceDto.getBookings();

        createEmptyCell(7);//linea en blanco
        // Add "Voucher" header
        addInternalHeader(table, "Voucher", "Hotel Reservation", "Contract", "Night Type", "", "", "");

        for (ManageBookingDto booking : bookings) {
            table.addCell(createCell(booking.getCouponNumber(), cellStyle));
            table.addCell(createCell(booking.getHotelBookingNumber(), cellStyle));
            table.addCell(createCell(booking.getContract(), cellStyle));
            table.addCell(createCell(booking.getNightType() != null ? booking.getNightType().getName() : "", cellStyle));
            table.addCell(createEmptyCell(3));
        }
        createEmptyCell(7);//linea en blanco
        // Add "Room Type" header
        addInternalHeader(table, "Room Type", "Rate Plan", "Room Number", "", "", "", "");

        for (ManageBookingDto booking : bookings) {
            table.addCell(createCell(booking.getRoomType() != null ? booking.getRoomType().getName() : "", cellStyle));
            table.addCell(createCell(booking.getRatePlan() != null ? booking.getRatePlan().getName() : "", cellStyle));
            table.addCell(createCell(booking.getRoomNumber(), cellStyle));
            table.addCell(createEmptyCell(4));
        }

        createEmptyCell(7);//linea en blanco
        // Add "Full Name" header
        addInternalHeader(table, "Full Name", "", "Remark", "", "", "", "");
        for (ManageBookingDto booking : bookings) {
            table.addCell(createCell(2,
                    booking.getFullName() != null ? booking.getFullName() : ""));// "Full Name" ocupa 2 celdas
            table.addCell(createCell(5,
                    booking.getHotelInvoiceNumber() != null ? booking.getHotelInvoiceNumber() : ""));// "Remark" ocupa las 5 celdas restantes
        }

        createEmptyCell(7);//linea en blanco

        BigDecimal total = new BigDecimal(0);
        String currency = invoiceDto.getHotel().getManageCurrency() != null
                ? invoiceDto.getHotel().getManageCurrency().getCode()
                        : "USD";
        addInternalHeader(table, "Check In", "Check Out", "Nights", "Adults", "Children", "Rate", "Currency");
        for (ManageBookingDto booking : bookings) {
            List<ManageRoomRateDto> roomRates = this.roomRateService.findByBooking(booking.getId());
            for (ManageRoomRateDto roomRate : roomRates) {
                BigDecimal invoiceAmount = roomRate.getInvoiceAmount() != null
                        ? BigDecimal.valueOf(roomRate.getInvoiceAmount())
                        : BigDecimal.ZERO;
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

        //added summary
        table.addCell(createEmptyCell(4));
        table.addCell(createCell("TOTAL", headerStyle));
        table.addCell(createCell("$ " + ScaleAmount.scaleAmount(total.doubleValue()),cellStyle));
        table.addCell(createCell(currency, cellStyle));
    }

    private void addInternalHeader(Table table, String... headers) {
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).addStyle(headerStyle)).setBorder(Border.NO_BORDER));
        }
    }

    private Cell createCell(String content, Style style) {
        return new Cell().add(new Paragraph(content != null ? content : "").addStyle(style)).setBorder(Border.NO_BORDER);
    }

    private Cell createCell(int colspan, String content) {
        return new Cell(1, colspan).add(new Paragraph(content != null ? content : "")
                .addStyle(cellStyle)).setBorder(Border.NO_BORDER);
    }

    private Cell createEmptyCell() {
        return new Cell().setBorder(Border.NO_BORDER);
    }

    private Cell createEmptyCell(int colspan) {
        return new Cell(1, colspan).setBorder(Border.NO_BORDER);
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
