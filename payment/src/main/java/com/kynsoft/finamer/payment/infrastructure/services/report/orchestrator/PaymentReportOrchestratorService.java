package com.kynsoft.finamer.payment.infrastructure.services.report.orchestrator;

import com.itextpdf.text.DocumentException;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.infrastructure.services.report.config.PaymentReportConfiguration;
import com.kynsoft.finamer.payment.infrastructure.services.report.data.PaymentDataExtractorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class PaymentReportOrchestratorService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentReportOrchestratorService.class);

    private final PaymentReportConfiguration configuration;
    private final PaymentDataExtractorService dataExtractorService;

    public PaymentReportOrchestratorService(PaymentReportConfiguration configuration,
                                            PaymentDataExtractorService dataExtractorService) {
        this.configuration = configuration;
        this.dataExtractorService = dataExtractorService;
    }

    public Optional<ByteArrayOutputStream> generateCombinedReport(
            Map<EPaymentReportType, IPaymentReport> reportServices,
            List<String> paymentIds) throws DocumentException, IOException {

        try {
            List<byte[]> finalReports = new ArrayList<>();

            for (String paymentIdStr : paymentIds) {
                UUID paymentId = UUID.fromString(paymentIdStr);
                logger.debug("Processing payment: {}", paymentId);

                try {
                    Optional<byte[]> paymentReport = generateSinglePaymentReport(reportServices, paymentId);
                    paymentReport.ifPresent(finalReports::add);
                } catch (Exception e) {
                    logger.error("Error generating report for payment: {}", paymentId, e);
                    // Continuar con los otros payments en caso de error
                }

                // Limpiar cache después de cada payment para optimizar memoria
                dataExtractorService.clearCache();
            }

            if (finalReports.isEmpty()) {
                logger.warn("No reports generated for any payment");
                return Optional.empty();
            }

            // Merge de todos los reportes de payments
            List<InputStream> finalStreams = finalReports.stream()
                    .map(ByteArrayInputStream::new)
                    .map(InputStream.class::cast)
                    .toList();

            return Optional.of(PDFUtils.mergePDF(finalStreams));

        } catch (Exception e) {
            logger.error("Error generating combined report", e);
            throw new RuntimeException("Failed to generate combined payment report", e);
        }
    }

    private Optional<byte[]> generateSinglePaymentReport(
            Map<EPaymentReportType, IPaymentReport> reportServices,
            UUID paymentId) throws Exception {

        List<byte[]> orderedContents = new ArrayList<>();

        // Generar reportes en el orden configurado
        for (EPaymentReportType reportType : configuration.getReportOrder()) {
            IPaymentReport service = reportServices.get(reportType);
            if (service != null) {
                try {
                    logger.debug("Generating {} report for payment: {}", reportType, paymentId);
                    service.generateReport(paymentId).ifPresent(orderedContents::add);
                } catch (Exception e) {
                    logger.error("Error generating {} report for payment: {}", reportType, paymentId, e);
                    // Continuar con los otros reportes en caso de error en uno específico
                }
            }
        }

        if (orderedContents.isEmpty()) {
            return Optional.empty();
        }

        if (orderedContents.size() == 1) {
            return Optional.of(orderedContents.get(0));
        }

        // Merge de todos los tipos de reporte para este payment
        List<InputStream> streams = orderedContents.stream()
                .map(ByteArrayInputStream::new)
                .map(InputStream.class::cast)
                .toList();

        return Optional.of(PDFUtils.mergePDFtoByte(streams));
    }
}