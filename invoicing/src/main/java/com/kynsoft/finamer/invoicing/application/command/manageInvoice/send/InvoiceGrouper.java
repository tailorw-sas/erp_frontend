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

    // Método principal que agrupa y genera los PDFs
    public List<GeneratedInvoice> generateInvoicesPDFs(List<ManageInvoiceDto> invoices, boolean groupByClient, boolean withAttachment)
            throws DocumentException, IOException {
        List<GeneratedInvoice> generatedInvoices = new ArrayList<>();

        // Agrupar facturas por B2BPartner
        Map<UUID, List<ManageInvoiceDto>> invoicesByB2BPartner = groupInvoicesByB2BPartner(invoices);

        for (Map.Entry<UUID, List<ManageInvoiceDto>> b2bPartnerEntry : invoicesByB2BPartner.entrySet()) {
            List<ManageInvoiceDto> partnerInvoices = b2bPartnerEntry.getValue();
            ManagerB2BPartnerDto b2bPartner = partnerInvoices.get(0).getAgency().getSentB2BPartner();

            if (groupByClient) {
                handleGroupByClient(generatedInvoices, partnerInvoices, b2bPartner, withAttachment);
            } else {
                handleGroupByHotel(generatedInvoices, partnerInvoices, b2bPartner, withAttachment);
            }
        }

        return generatedInvoices;
    }

    // Agrupar facturas por B2BPartner
    private Map<UUID, List<ManageInvoiceDto>> groupInvoicesByB2BPartner(List<ManageInvoiceDto> invoices) {
        return invoices.stream()
                .collect(Collectors.groupingBy(invoice -> invoice.getAgency().getSentB2BPartner().getId()));
    }

    // Manejar agrupación por cliente
    private void handleGroupByClient(List<GeneratedInvoice> generatedInvoices, List<ManageInvoiceDto> partnerInvoices, ManagerB2BPartnerDto b2bPartner, boolean withAttachment) throws DocumentException, IOException {
        Map<UUID, List<ManageInvoiceDto>> invoicesByClient = partnerInvoices.stream()
                .collect(Collectors.groupingBy(invoice -> invoice.getAgency().getClient().getId()));

        for (List<ManageInvoiceDto> clientInvoices : invoicesByClient.values()) {
            ByteArrayOutputStream pdfStream = generatePDF(clientInvoices, withAttachment);
            String nameFile = generateFileName(clientInvoices, true);
            generatedInvoices.add(new GeneratedInvoice(pdfStream, nameFile, b2bPartner.getIp(), b2bPartner.getUserName(), b2bPartner.getPassword(), clientInvoices));
        }
    }

    // Manejar agrupación por hotel
    private void handleGroupByHotel(List<GeneratedInvoice> generatedInvoices, List<ManageInvoiceDto> partnerInvoices, ManagerB2BPartnerDto b2bPartner, boolean withAttachment) throws DocumentException, IOException {
        Map<UUID, List<ManageInvoiceDto>> invoicesByHotel = partnerInvoices.stream()
                .collect(Collectors.groupingBy(invoice -> invoice.getHotel().getId()));

        for (List<ManageInvoiceDto> hotelInvoices : invoicesByHotel.values()) {
            for (ManageInvoiceDto invoice : hotelInvoices) {
                List<ManageInvoiceDto> invoiceDtos = new ArrayList<>();
                invoiceDtos.add(invoice);
                ByteArrayOutputStream pdfStream = generatePDF(invoiceDtos, withAttachment);
                String nameFile = generateFileName(invoiceDtos, false);
                generatedInvoices.add(new GeneratedInvoice(pdfStream, nameFile, b2bPartner.getIp(), b2bPartner.getUserName(), b2bPartner.getPassword(), hotelInvoices));
            }
        }
    }

    // Generar PDF para una lista de facturas
    public ByteArrayOutputStream generatePDF(List<ManageInvoiceDto> invoices, boolean withAttachment) throws DocumentException, IOException {
        ByteArrayOutputStream combinedPdfOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, combinedPdfOutputStream);
        document.open();

        for (ManageInvoiceDto invoice : invoices) {
            Optional<ByteArrayOutputStream> invoicePdf = getInvoicesBooking(invoice.getId().toString(), withAttachment);
            if (invoicePdf.isPresent()) {
                ByteArrayInputStream invoicePdfStream = new ByteArrayInputStream(invoicePdf.get().toByteArray());
                PdfReader reader = new PdfReader(invoicePdfStream);

                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    copy.addPage(copy.getImportedPage(reader, i));
                }
                reader.close();
            }
        }

        document.close();
        return combinedPdfOutputStream;
    }

    // Obtener el contenido de las facturas
    private Optional<ByteArrayOutputStream> getInvoicesBooking(String invoiceIds, boolean isWithAttachment) throws DocumentException, IOException {
        ByteArrayOutputStream combinedOutputStream = new ByteArrayOutputStream();
        if (isWithAttachment) {
            combinedOutputStream=PDFUtils.mergePDF(getReportContent(invoiceIds,EInvoiceReportType.INVOICE_AND_BOOKING, EInvoiceReportType.INVOICE_SUPPORT));
            //combineReports(invoiceIds, combinedOutputStream, EInvoiceReportType.INVOICE_AND_BOOKING, EInvoiceReportType.INVOICE_SUPPORT);
        } else {
            combineReports(invoiceIds, combinedOutputStream, EInvoiceReportType.INVOICE_AND_BOOKING);
        }
        return combinedOutputStream.size() > 0 ? Optional.of(combinedOutputStream) : Optional.empty();
    }

    private List<InputStream> getReportContent(String invoiceIds,EInvoiceReportType... reportTypes) throws DocumentException, IOException {
        List<InputStream> result = new ArrayList<>();
        for (EInvoiceReportType reportType : reportTypes) {
            IInvoiceReport reportService = invoiceReportProviderFactory.getInvoiceReportService(reportType);
            Optional<Map<String, byte[]>> response = getReportContent(reportService, invoiceIds);
            if (response.isPresent() && !response.get().isEmpty()) {
               result.add(new ByteArrayInputStream(response.get().values().iterator().next()));
            }
        }
        return result;
    }

    // Combinar los reportes en el output stream
    private void combineReports(String invoiceIds, ByteArrayOutputStream combinedOutputStream, EInvoiceReportType... reportTypes) throws DocumentException, IOException {
        // Crear un nuevo documento que almacenará todas las páginas combinadas
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, combinedOutputStream);
        document.open();

        for (EInvoiceReportType reportType : reportTypes) {
            IInvoiceReport reportService = invoiceReportProviderFactory.getInvoiceReportService(reportType);
            Optional<Map<String, byte[]>> response = getReportContent(reportService, invoiceIds);
            if (response.isPresent() && !response.get().isEmpty()) {

                byte[] content = response.get().values().iterator().next();
                // Cargar el PDF actual desde los bytes recibidos
                ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
                PdfReader reader = new PdfReader(inputStream);

                // Agregar todas las páginas del PDF actual al documento combinado
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    copy.addPage(copy.getImportedPage(reader, i));
                }
                reader.close();
            }
        }

        // Cerrar el documento combinado
        document.close();
    }

    private Optional<Map<String, byte[]>> getReportContent(IInvoiceReport reportService, String invoiceId) throws DocumentException, IOException {
        Map<String, byte[]> result = new HashMap<>();
        Optional<byte[]> reportContent = reportService.generateReport(invoiceId);

        reportContent.ifPresent(bytes -> result.put(invoiceId, bytes));
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    // Generar el nombre de archivo en función de si es agrupado por cliente o no
    private String generateFileName(List<ManageInvoiceDto> invoices, boolean groupByClient) {
        ManageInvoiceDto firstInvoice = invoices.get(0);

        if (groupByClient) {
            String clientName = firstInvoice.getAgency().getClient().getName();
            LocalDate fromDate = invoices.stream().map(invoice -> invoice.getInvoiceDate().toLocalDate()).min(LocalDate::compareTo).orElse(LocalDate.now());
            LocalDate toDate = invoices.stream().map(invoice -> invoice.getInvoiceDate().toLocalDate()).max(LocalDate::compareTo).orElse(LocalDate.now());
            return clientName + "-INV From " + formatDate(fromDate) + " To " + formatDate(toDate) + ".pdf";
        } else {
            String agencyName = firstInvoice.getAgency().getName();
            String invoiceNumber = firstInvoice.getInvoiceNumber();
            LocalDate invoiceDate = firstInvoice.getInvoiceDate().toLocalDate();
            return invoiceNumber + "-" + agencyName + "-" + formatDate(invoiceDate) + ".pdf";
        }
    }

    // Formatear la fecha como MMDDYY
    private String formatDate(LocalDate date) {
        return String.format("%02d%02d%02d", date.getMonthValue(), date.getDayOfMonth(), date.getYear() % 100);
    }

}