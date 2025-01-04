package com.kynsoft.finamer.invoicing.application.query.report;

import com.itextpdf.text.DocumentException;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.util.ReportUtil;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InvoiceMergeReportQueryHandler implements IQueryHandler<InvoiceMergeReportQuery, InvoiceMergeReportResponse> {
    private final InvoiceReportProviderFactory invoiceReportProviderFactory;

    public InvoiceMergeReportQueryHandler(InvoiceReportProviderFactory invoiceReportProviderFactory) {
        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
    }

    @Override
    public InvoiceMergeReportResponse handle(InvoiceMergeReportQuery query) {
        InvoiceReportRequest invoiceReportRequest = query.getInvoiceReportRequest();
        List<EInvoiceReportType> invoiceReportTypes = getInvoiceTypeFromRequest(invoiceReportRequest);
        Map<EInvoiceReportType, IInvoiceReport> services = getServiceByInvoiceType(invoiceReportTypes);
        try {
           Optional<ByteArrayOutputStream> reportContent = getMergeReportContent(services, Arrays.asList(invoiceReportRequest.getInvoiceId()));
           if (reportContent.isPresent()) {
               return ReportUtil.createMergePaymentReportResponse(reportContent.get().toByteArray(), invoiceReportRequest.getInvoiceId().length > 0 ?
                       "invoicing-report.pdf" : invoiceReportRequest.getInvoiceId()[0] + ".pdf");
           }
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private List<EInvoiceReportType> getInvoiceTypeFromRequest(InvoiceReportRequest invoiceReportRequest) {
        return Arrays.stream(invoiceReportRequest.getInvoiceType())
                .map(EInvoiceReportType::valueOf)
                .toList();
    }

    private Map<EInvoiceReportType, IInvoiceReport> getServiceByInvoiceType(List<EInvoiceReportType> types) {
        Map<EInvoiceReportType, IInvoiceReport> services = new HashMap<>();
        for (EInvoiceReportType type : types) {
            services.put(type, invoiceReportProviderFactory.getInvoiceReportService(type));
        }
        return services;
    }

    private Optional<ByteArrayOutputStream> getMergeReportContent(Map<EInvoiceReportType, IInvoiceReport> reportService, List<String> invoicesIds) throws DocumentException, IOException {
        Map<EInvoiceReportType, Optional<byte[]>> reportContent = new HashMap<>();
        List<byte[]> result = new ArrayList<>();
        for (String invoicesId : invoicesIds) {
            for (Map.Entry<EInvoiceReportType, IInvoiceReport> entry : reportService.entrySet()) {
                reportContent.put(entry.getKey(), entry.getValue().generateReport(invoicesId));
            }
            List<Optional<byte[]>> orderedContent = getOrderReportContent(reportContent);
            List<InputStream> finalContent = orderedContent.stream()
                    .filter(Optional::isPresent)
                    .map(content -> new ByteArrayInputStream(content.get()))
                    .map(InputStream.class::cast)
                    .toList();
            if (!finalContent.isEmpty()) {
                result.add(PDFUtils.mergePDFtoByte(finalContent));
            }
        }
        if (!result.isEmpty())
            return Optional.of(PDFUtils.mergePDF(result.stream().map(ByteArrayInputStream::new).map(InputStream.class::cast).toList()));
        return Optional.empty();
    }


    private List<Optional<byte[]>> getOrderReportContent(Map<EInvoiceReportType, Optional<byte[]>> content) {
        List<Optional<byte[]>> orderedContent = new LinkedList<>();
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_AND_BOOKING, Optional.empty()));
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_SUPPORT, Optional.empty()));
        return orderedContent;
    }
}
