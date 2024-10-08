package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InvoiceGrouper {

    private final InvoiceReportProviderFactory invoiceReportProviderFactory;

    public InvoiceGrouper(InvoiceReportProviderFactory invoiceReportProviderFactory) {
        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
    }

    public List<GeneratedInvoice> generateInvoicesPDFs(List<ManageInvoiceDto> invoices, boolean groupByClient, boolean withAttachment)
            throws DocumentException, IOException {
        List<GeneratedInvoice> generatedInvoices = new ArrayList<>();

        // Agrupar facturas por B2BPartner
        Map<UUID, List<ManageInvoiceDto>> invoicesByB2BPartner = invoices.stream()
                .collect(Collectors.groupingBy(invoice -> invoice.getAgency().getSentB2BPartner().getId()));

        for (Map.Entry<UUID, List<ManageInvoiceDto>> b2bPartnerEntry : invoicesByB2BPartner.entrySet()) {
            UUID b2bPartnerId = b2bPartnerEntry.getKey();
            List<ManageInvoiceDto> partnerInvoices = b2bPartnerEntry.getValue();

            ManagerB2BPartnerDto b2bPartner = partnerInvoices.get(0).getAgency().getSentB2BPartner();  // Asumimos que todas tienen el mismo B2BPartner

            if (groupByClient) {
                // Agrupamos por cliente
                Map<UUID, List<ManageInvoiceDto>> invoicesByClient = partnerInvoices.stream()
                        .collect(Collectors.groupingBy(invoice -> invoice.getAgency().getClient().getId()));

                for (List<ManageInvoiceDto> clientInvoices : invoicesByClient.values()) {
                    ByteArrayOutputStream pdfStream = generatePDF(clientInvoices, withAttachment);
                    String nameFile = generateFileName(clientInvoices, true); // Genera el nombre correcto
                    generatedInvoices.add(new GeneratedInvoice(pdfStream, nameFile, b2bPartner.getIp(), b2bPartner.getUserName(), b2bPartner.getPassword(), clientInvoices));
                }
            } else {
                // Agrupamos por hotel
                Map<UUID, List<ManageInvoiceDto>> invoicesByHotel = partnerInvoices.stream()
                        .collect(Collectors.groupingBy(invoice -> invoice.getHotel().getId()));

                for (List<ManageInvoiceDto> hotelInvoices : invoicesByHotel.values()) {
                    ByteArrayOutputStream pdfStream = generatePDF(hotelInvoices, withAttachment);
                    String nameFile = generateFileName(hotelInvoices, false); // Genera el nombre correcto
                    generatedInvoices.add(new GeneratedInvoice(pdfStream, nameFile, b2bPartner.getIp(), b2bPartner.getUserName(), b2bPartner.getPassword(), hotelInvoices));
                }
            }
        }

        return generatedInvoices;
    }

    public ByteArrayOutputStream generatePDF(List<ManageInvoiceDto> invoices, boolean withAttachment) throws DocumentException, IOException {
        // Crear un nuevo OutputStream para almacenar el PDF combinado
        ByteArrayOutputStream combinedPdfOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, combinedPdfOutputStream);
        document.open();

        // Iterar sobre las facturas y combinar sus PDFs
        for (ManageInvoiceDto invoice : invoices) {
            Optional<ByteArrayOutputStream> invoicePdf = getInvoicesBooking(invoice.getId().toString(), withAttachment);
            if (invoicePdf.isPresent()) {
                ByteArrayInputStream invoicePdfStream = new ByteArrayInputStream(invoicePdf.get().toByteArray());
                PdfReader reader = new PdfReader(invoicePdfStream);

                // Añadir cada página del PDF al documento combinado
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    copy.addPage(copy.getImportedPage(reader, i));
                }
                reader.close();
            }
        }

        // Cerrar el documento combinado
        document.close();
        return combinedPdfOutputStream;
    }


    private Optional<ByteArrayOutputStream> getInvoicesBooking(String invoiceIds, boolean isWithAttachment) throws DocumentException, IOException {
        EInvoiceReportType reportType = isWithAttachment
                ? EInvoiceReportType.INVOICE_AND_BOOKING
                : EInvoiceReportType.INVOICE_SUPPORT;
        Map<EInvoiceReportType, IInvoiceReport> services = new HashMap<>();
        services.put(reportType, invoiceReportProviderFactory.getInvoiceReportService(reportType));

        Optional<Map<String, byte[]>> response = getReportContent(services, invoiceIds);

        if (response.isPresent() && !response.get().isEmpty()) {
            byte[] content = response.get().values().iterator().next();

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                outputStream.write(content);
                return Optional.of(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        } else {
            System.out.println("No se pudo obtener el contenido del reporte.");
            return Optional.empty();
        }
    }

    private Optional<Map<String, byte[]>> getReportContent(Map<EInvoiceReportType, IInvoiceReport> reportService, String invoiceId) throws DocumentException, IOException {
        Map<String, byte[]> result = new HashMap<>();

        Map<EInvoiceReportType, Optional<byte[]>> reportContent = reportService.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().generateReport(invoiceId)
                ));

        List<InputStream> finalContent = getOrderReportContent(reportContent).stream()
                .filter(Optional::isPresent)
                .map(content -> new ByteArrayInputStream(content.get()))
                .map(InputStream.class::cast)
                .toList();
        if (!finalContent.isEmpty()) {
            result.put(invoiceId, PDFUtils.mergePDFtoByte(finalContent));
        }

        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    private List<Optional<byte[]>> getOrderReportContent(Map<EInvoiceReportType, Optional<byte[]>> content) {
        List<Optional<byte[]>> orderedContent = new LinkedList<>();
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_AND_BOOKING, Optional.empty()));
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_SUPPORT, Optional.empty()));
        return orderedContent;
    }

    private String generateFileName(List<ManageInvoiceDto> invoices, boolean groupByClient) {
        ManageInvoiceDto firstInvoice = invoices.get(0);

        if (groupByClient) {
            // Nombre del archivo para agrupar por cliente
            String clientName = firstInvoice.getAgency().getClient().getName();

            // Convertir LocalDateTime a LocalDate para las comparaciones
            LocalDate fromDate = invoices.stream()
                    .map(invoice -> invoice.getInvoiceDate().toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now()); // Obtener la fecha mínima de las facturas

            LocalDate toDate = invoices.stream()
                    .map(invoice -> invoice.getInvoiceDate().toLocalDate())
                    .max(LocalDate::compareTo)
                    .orElse(LocalDate.now()); // Obtener la fecha máxima de las facturas

            return clientName + "-INV From " + formatDate(fromDate) + " To " + formatDate(toDate) + ".pdf";
        } else {
            // Nombre del archivo para facturas individuales por agencia
            String agencyName = firstInvoice.getAgency().getName();
            String invoiceNumber = firstInvoice.getInvoiceNumber();
            LocalDate invoiceDate = firstInvoice.getInvoiceDate().toLocalDate();
            return invoiceNumber + " " + agencyName + " " + formatDate(invoiceDate) + ".pdf";
        }
    }

    // Método auxiliar para formatear la fecha en MMDDYY
    private String formatDate(LocalDate date) {
        return String.format("%02d%02d%02d", date.getMonthValue(), date.getDayOfMonth(), date.getYear() % 100);
    }
}