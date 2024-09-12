package com.kynsoft.finamer.invoicing.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceContentProvider;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Component
public class ReportContentProviderFactory {

    private final RestTemplate restTemplate;
    private final IReportGenerator reportGenerator;

    private final IManageAttachmentService manageAttachmentService;

    public ReportContentProviderFactory(RestTemplate restTemplate, IReportGenerator reportGenerator, IManageAttachmentService manageAttachmentService) {
        this.restTemplate = restTemplate;
        this.reportGenerator = reportGenerator;

        this.manageAttachmentService = manageAttachmentService;
    }

    public AbstractReportContentProvider getReportContentProvider(EInvoiceContentProvider paymentContentProvider) {

        switch (paymentContentProvider) {
            case INVOICE_SUPPORT_CONTENT -> {
                return new InvoiceSupportProvider(restTemplate,reportGenerator,manageAttachmentService);
            }
            case INVOICE_AND_BOOKING_CONTENT -> {
                return  new InvoiceAndBookingContentProvider(restTemplate,reportGenerator);
            }
            default -> throw new NoSuchElementException();
        }
    }
}
