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

@Service(PaymentReportPaymentDetailsService.BEAN_ID)
public class PaymentReportPaymentDetailsService extends AbstractBasePaymentReportService {

    public static final String BEAN_ID = "PAYMENT_DETAILS";

    private final ReportContentProviderFactory reportContentProviderFactory;

    public PaymentReportPaymentDetailsService(PaymentDataExtractorService dataExtractorService,
                                              PaymentReportConfiguration configuration,
                                              ReportContentProviderFactory reportContentProviderFactory) {
        super(dataExtractorService, configuration);
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    protected List<byte[]> generateReportContents(UUID paymentId) {
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.PAYMENT_DETAILS_REPORT_CONTENT);

        String reportCode = configuration.getReportCode(EPaymentReportType.PAYMENT_DETAILS);
        Optional<byte[]> content = contentProvider.getContent(
                createParameters("paymentId", paymentId),
                reportCode
        );

        return filterValidContents(List.of(content));
    }
}
