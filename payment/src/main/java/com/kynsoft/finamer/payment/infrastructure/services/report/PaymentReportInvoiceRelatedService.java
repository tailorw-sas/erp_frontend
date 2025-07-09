package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.infrastructure.services.report.base.AbstractBasePaymentReportService;
import com.kynsoft.finamer.payment.infrastructure.services.report.config.PaymentReportConfiguration;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.ReportContentProviderFactory;
import com.kynsoft.finamer.payment.infrastructure.services.report.data.PaymentDataExtractorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service(PaymentReportInvoiceRelatedService.BEAN_ID)
public class PaymentReportInvoiceRelatedService extends AbstractBasePaymentReportService {

    public static final String BEAN_ID = "INVOICE_RELATED";

    private final ReportContentProviderFactory reportContentProviderFactory;

    public PaymentReportInvoiceRelatedService(PaymentDataExtractorService dataExtractorService,
                                              PaymentReportConfiguration configuration,
                                              ReportContentProviderFactory reportContentProviderFactory) {
        super(dataExtractorService, configuration);
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    protected List<byte[]> generateReportContents(UUID paymentId) {
        String commaSeparatedInvoiceIds = dataExtractorService.getCommaSeparatedInvoiceIds(paymentId);

        if (commaSeparatedInvoiceIds.isEmpty()) {
            logger.debug("No related invoices found for payment: {}", paymentId);
            return List.of();
        }

        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.INVOICE_REPORT_CONTENT);

        String reportCode = configuration.getReportCode(EPaymentReportType.INVOICE_RELATED);
        Optional<byte[]> content = contentProvider.getContent(
                createParameters("idInvoice", commaSeparatedInvoiceIds),
                reportCode
        );

        return filterValidContents(List.of(content));
    }
}
