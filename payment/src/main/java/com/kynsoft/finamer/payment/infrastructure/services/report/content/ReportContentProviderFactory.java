package com.kynsoft.finamer.payment.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Component
public class ReportContentProviderFactory {

    private final RestTemplate restTemplate;
    private final IReportGenerator reportGenerator;
    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    @Value("${report.payment-all-support-code}")
    private String paymentAllSupportCode;
    @Value("${report.invoice.service}")
    private String invoiceServiceURL;
    public ReportContentProviderFactory(RestTemplate restTemplate, IReportGenerator reportGenerator,
                                        IManageAttachmentTypeService attachmentTypeService,
                                        IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                        IManageAttachmentTypeService manageAttachmentTypeService) {
        this.restTemplate = restTemplate;
        this.reportGenerator = reportGenerator;
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
    }

    public AbstractReportContentProvider getReportContentProvider(EPaymentContentProvider paymentContentProvider) {

        switch (paymentContentProvider) {
            case PAYMENT_DETAILS_REPORT_CONTENT -> {
                return new PaymentAndDetailsReportContentProvider(restTemplate, reportGenerator);
            }
            case INVOICE_REPORT_CONTENT -> {
                return new InvoiceReportContentProvider(restTemplate, reportGenerator);
            }
            case INVOICE_ATTACHMENT_CONTENT -> {
                return new InvoiceAttachmentContentProvider(restTemplate, reportGenerator, invoiceServiceURL);
            }
            case PAYMENT_SUPPORT_CONTENT -> {
                return new PaymentAttachmentContentProvider(restTemplate,reportGenerator,
                        masterPaymentAttachmentService,manageAttachmentTypeService,paymentAllSupportCode);
            }
            case PAYMENT_ALL_SUPPORT_CONTENT -> {
                return new PaymentAllAttachmentContentProvider(restTemplate,reportGenerator,
                        masterPaymentAttachmentService);
            }
            default -> throw new NoSuchElementException();
        }
    }
}
