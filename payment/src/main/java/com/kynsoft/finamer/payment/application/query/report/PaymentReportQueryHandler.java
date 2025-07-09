package com.kynsoft.finamer.payment.application.query.report;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.infrastructure.services.factory.PaymentReportProviderFactory;
import com.kynsoft.finamer.payment.infrastructure.services.report.orchestrator.PaymentReportOrchestratorService;
import com.kynsoft.finamer.payment.infrastructure.services.report.util.ReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Component
public class PaymentReportQueryHandler implements IQueryHandler<PaymentReportQuery, PaymentReportResponse> {

    private static final Logger logger = LoggerFactory.getLogger(PaymentReportQueryHandler.class);

    private final PaymentReportProviderFactory paymentReportProviderFactory;
    private final PaymentReportOrchestratorService orchestratorService;

    public PaymentReportQueryHandler(PaymentReportProviderFactory paymentReportProviderFactory,
                                     PaymentReportOrchestratorService orchestratorService) {
        this.paymentReportProviderFactory = paymentReportProviderFactory;
        this.orchestratorService = orchestratorService;
    }

    @Override
    public PaymentReportResponse handle(PaymentReportQuery query) {
        try {
            PaymentReportRequest request = query.getPaymentReportRequest();
            logger.info("Processing payment report request for {} payments with {} report types",
                    request.getPaymentId().length, request.getPaymentType().length);

            List<EPaymentReportType> reportTypes = validateAndParseReportTypes(request.getPaymentType());
            Map<EPaymentReportType, IPaymentReport> reportServices = getReportServices(reportTypes);

            Optional<ByteArrayOutputStream> reportContent = orchestratorService.generateCombinedReport(
                    reportServices,
                    Arrays.asList(request.getPaymentId())
            );

            if (reportContent.isPresent()) {
                String fileName = generateFileName(request.getPaymentId());
                return ReportUtil.createPaymentReportResponse(reportContent.get().toByteArray(), fileName);
            }

            logger.warn("No report content generated for request");
            return null;

        } catch (Exception e) {
            logger.error("Error handling payment report query", e);
            throw new RuntimeException("Failed to generate payment report", e);
        }
    }

    private List<EPaymentReportType> validateAndParseReportTypes(String[] reportTypeNames) {
        List<EPaymentReportType> reportTypes = new ArrayList<>();

        for (String typeName : reportTypeNames) {
            try {
                EPaymentReportType reportType = EPaymentReportType.valueOf(typeName);
                if (paymentReportProviderFactory.isServiceAvailable(reportType)) {
                    reportTypes.add(reportType);
                } else {
                    logger.warn("Report service not available for type: {}", reportType);
                }
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid report type: {}", typeName);
            }
        }

        if (reportTypes.isEmpty()) {
            throw new IllegalArgumentException("No valid report types provided");
        }

        return reportTypes;
    }

    private Map<EPaymentReportType, IPaymentReport> getReportServices(List<EPaymentReportType> reportTypes) {
        Map<EPaymentReportType, IPaymentReport> services = new EnumMap<>(EPaymentReportType.class);

        for (EPaymentReportType type : reportTypes) {
            services.put(type, paymentReportProviderFactory.getPaymentReportService(type));
        }

        return services;
    }

    private String generateFileName(String[] paymentIds) {
        if (paymentIds.length == 1) {
            return paymentIds[0] + ".pdf";
        }
        return "payment-report-" + paymentIds.length + "-payments.pdf";
    }
}