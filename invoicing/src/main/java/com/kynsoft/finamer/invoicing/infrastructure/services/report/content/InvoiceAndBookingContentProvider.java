package com.kynsoft.finamer.invoicing.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

public class InvoiceAndBookingContentProvider extends AbstractReportContentProvider {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceAndBookingContentProvider.class);


    public InvoiceAndBookingContentProvider(RestTemplate restTemplate, IReportGenerator reportGenerator) {
        super(restTemplate, reportGenerator);
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        return Optional.ofNullable(reportGenerator.generateReport(parameters, "inv"));

    }


}
