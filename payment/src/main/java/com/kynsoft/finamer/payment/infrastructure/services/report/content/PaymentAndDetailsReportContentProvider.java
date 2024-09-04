package com.kynsoft.finamer.payment.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

public class PaymentAndDetailsReportContentProvider extends AbstractReportContentProvider{
    public PaymentAndDetailsReportContentProvider(RestTemplate restTemplate, IReportGenerator reportGenerator) {
        super(restTemplate, reportGenerator);
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        return Optional.ofNullable(reportGenerator.generateReport(parameters, "aaa"));
    }
}
