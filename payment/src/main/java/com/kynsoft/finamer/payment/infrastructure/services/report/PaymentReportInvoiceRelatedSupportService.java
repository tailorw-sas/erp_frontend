package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.infrastructure.services.report.base.AbstractBasePaymentReportService;
import com.kynsoft.finamer.payment.infrastructure.services.report.config.PaymentReportConfiguration;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.ReportContentProviderFactory;
import com.kynsoft.finamer.payment.infrastructure.services.report.data.PaymentDataExtractorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service(PaymentReportInvoiceRelatedSupportService.BEAN_ID)
public class PaymentReportInvoiceRelatedSupportService extends AbstractBasePaymentReportService {

    public static final String BEAN_ID = "INVOICE_RELATED_SUPPORT";

    private final ReportContentProviderFactory reportContentProviderFactory;

    public PaymentReportInvoiceRelatedSupportService(PaymentDataExtractorService dataExtractorService,
                                                     PaymentReportConfiguration configuration,
                                                     ReportContentProviderFactory reportContentProviderFactory) {
        super(dataExtractorService, configuration);
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    protected List<byte[]> generateReportContents(UUID paymentId) {
        List<UUID> invoiceIds = dataExtractorService.getRelatedInvoiceIds(paymentId);

        if (invoiceIds.isEmpty()) {
            logger.debug("No related invoices found for payment: {}", paymentId);
            return List.of();
        }

        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.INVOICE_ATTACHMENT_CONTENT);

        String reportCode = configuration.getReportCode(EPaymentReportType.INVOICE_RELATED_SUPPORT);

        List<Optional<byte[]>> contents = new ArrayList<>();
        for (UUID invoiceId : invoiceIds) {
            Optional<byte[]> content = contentProvider.getContent(
                    createParameters("invoiceId", invoiceId.toString()),
                    reportCode
            );
            contents.add(content);
        }

        return filterValidContents(contents);
    }
}
