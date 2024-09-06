package com.kynsoft.finamer.payment.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsoft.finamer.payment.infrastructure.services.report.util.ReportUtil;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

public class InvoiceReportContentProvider extends AbstractReportContentProvider {

    public InvoiceReportContentProvider(RestTemplate restTemplate,
                                        IReportGenerator reportGenerator) {
        super(restTemplate,reportGenerator);
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        return getInvoiceReport();
    }

    private Optional<byte[]> getInvoiceReport() {
        return Optional.of(ReportUtil.defaultPdfContent());
    }



}
