package com.kynsoft.finamer.invoicing.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class InvoiceReconcileAutomaticProvider extends AbstractReportContentProvider{
    public InvoiceReconcileAutomaticProvider(RestTemplate restTemplate, IReportGenerator reportGenerator) {
        super(restTemplate, reportGenerator);
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        return Optional.ofNullable(reportGenerator.generateReport(parameters, "rec"));
    }
}
