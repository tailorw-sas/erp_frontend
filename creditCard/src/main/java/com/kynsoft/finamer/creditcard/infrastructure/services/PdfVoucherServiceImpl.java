package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IPdfVoucherService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfVoucherServiceImpl implements IPdfVoucherService {



    @Override
    public byte[] generatePdf(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto) throws IOException {
        // Variables that are used in the creation of the pdf
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos); // Write to ByteArrayOutputStream
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.LETTER);
        Document document = new Document(pdfDoc);

        // Agregar logo
//        String logoPath = "path/to/logo.png"; // Cambia a la ruta de tu logo
//        ImageData logoData = ImageDataFactory.create(logoPath);
//        Image logo = new Image(logoData).scaleToFit(100, 50);
//        logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
//        document.add(logo);

        // Agregar título
        Paragraph title = new Paragraph("TRANSACTION INFORMATION")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16)
                .setBold()
                .setMarginBottom(10)
                .setFontColor(new DeviceRgb(35, 153, 181));
        document.add(title);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String transactionDateStr = transactionDto.getPaymentDate() != null ? transactionDto.getPaymentDate().format(formatter) : "";
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedAmount = currencyFormatter.format(transactionDto.getAmount());

        // Crear una tabla para los detalles
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 5}))
                .useAllAvailableWidth();

        // Agregar contenido a la tabla
        addTableRow(table, "Commerce:", merchantConfigDto.getName());
        addTableRow(table, "Merchant:", transactionDto.getMerchant().getDescription());
        addTableRow(table, "Hotel:", transactionDto.getHotel().getName());
        addTableRow(table, "Number ID:", transactionDto.getId().toString());
        addTableRow(table, "Transaction Type:", "Sale");
        addTableRow(table, "Status:", transactionDto.getStatus().getName());
        addTableRow(table, "Payment Date:", transactionDateStr);
        addTableRow(table, "Authorization Number:", "N/A");
        addTableRow(table, "Card Type:", transactionDto.getCreditCardType() != null ? transactionDto.getCreditCardType().getName() : "");
        addTableRow(table, "Card Number:", transactionDto.getCardNumber());
        addTableRow(table, "Amount (USD):", formattedAmount);
        addTableRow(table, "Ibtis (USD):", "$0.00");
        addTableRow(table, "Reference:", transactionDto.getReferenceNumber());
        addTableRow(table, "User:", transactionDto.getGuestName());
        addTableRow(table, "Modality:", transactionDto.getMethodType().name());

        document.add(table);

        // Agregar mensaje final
        Paragraph footer = new Paragraph("Thank you for your purchase")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setMarginTop(20);
        document.add(footer);

        // Agregar un manejador de eventos para personalizar el footer
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterEventHandler());
        // Cerrar documento
        document.close();

        // Devolver el contenido del ByteArrayOutputStream como byte[]
        return baos.toByteArray();
    }

    private void addTableRow(Table table, String key, String value) {
        table.addCell(new Cell().add(new Paragraph(key).setBold()).setBorder(Border.NO_BORDER.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(value)).setBorder(Border.NO_BORDER));
    }

    // Clase interna para manejar el evento del footer
    private static class FooterEventHandler implements IEventHandler {
        private final Color backgroundColor = new DeviceRgb(35, 153, 181); // Turquesa
        private final Color textColor = new DeviceRgb(255,255,255); // Blanco
        private final float margin = 36f;

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDocument = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            PdfCanvas canvas = new PdfCanvas(page);

            // Dimensiones de la página
            Rectangle pageSize = page.getPageSize();
            float x = pageSize.getLeft() + margin; // Ajustar el inicio del rectángulo con el margen
            float y = pageSize.getBottom()+ margin;
            float width = pageSize.getWidth() - 2 * margin; // Reducir el ancho para dejar márgenes
            float height = 30f; // Altura del footer

            // Dibujar el fondo del footer
            canvas.saveState()
                    .setFillColor(backgroundColor)
                    .rectangle(x, y, width, height)
                    .fill()
                    .restoreState();

            // Dibujar el texto del footer
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String footerText = "© 2024 FINAMER. All rights reserved.";
            float textWidth = font.getWidth(footerText, 10); // Obtener el ancho del texto
            float textX = (pageSize.getWidth() - textWidth) / 2; // Calcular posición centrada

            canvas.beginText()
                    .setFontAndSize(font, 10)
                    .setColor(textColor, true)
                    .moveText(textX, y + height / 3) // Alinear al centro horizontal
                    .showText(footerText)
                    .endText()
                    .release();
        }
    }
}
