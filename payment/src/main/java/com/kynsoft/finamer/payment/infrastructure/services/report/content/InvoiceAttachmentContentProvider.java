package com.kynsoft.finamer.payment.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsoft.finamer.payment.application.query.report.InvoiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class InvoiceAttachmentContentProvider extends AbstractReportContentProvider {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceAttachmentContentProvider.class);

    private final String INVOICE_SERVICE_URL;

    public InvoiceAttachmentContentProvider(RestTemplate restTemplate, IReportGenerator reportGenerator, String invoiceServiceUrl) {
        super(restTemplate, reportGenerator);
        INVOICE_SERVICE_URL = invoiceServiceUrl;
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        String invoiceId = (String) parameters.getOrDefault("invoiceRelatedId", "");
        Assert.notNull(invoiceId, "El id del invoice es obligatorio");
        try {
            return getInvoiceAndBookingAndSupportReportContent(invoiceId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }


    private Optional<byte[]> getInvoiceAndBookingAndSupportReportContent(String invoiceId) throws IOException {
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setInvoiceId(invoiceId);
        invoiceRequest.setInvoiceType("INVOICE_SUPPORT");
        ResponseEntity<byte[]> response =
                restTemplate.postForEntity(INVOICE_SERVICE_URL + "/api/invoice/report",
                        invoiceRequest, byte[].class);
        if (response.getStatusCode().is2xxSuccessful()){
            return Optional.ofNullable(response.getBody());
        }
        return Optional.empty();
    }
}
