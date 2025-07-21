package com.kynsoft.finamer.payment.infrastructure.services.report.base;

import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.infrastructure.services.report.config.PaymentReportConfiguration;
import com.kynsoft.finamer.payment.infrastructure.services.report.data.PaymentDataExtractorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

public abstract class AbstractBasePaymentReportService implements IPaymentReport {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final PaymentDataExtractorService dataExtractorService;
    protected final PaymentReportConfiguration configuration;

    protected AbstractBasePaymentReportService(PaymentDataExtractorService dataExtractorService,
                                               PaymentReportConfiguration configuration) {
        this.dataExtractorService = dataExtractorService;
        this.configuration = configuration;
    }

    @Override
    public final Optional<byte[]> generateReport(UUID paymentId) {
        try {
            logger.debug("Generating report for payment: {}", paymentId);

            List<byte[]> contents = generateReportContents(paymentId);

            if (contents.isEmpty()) {
                logger.warn("No content generated for payment: {}", paymentId);
                return Optional.empty();
            }

            if (contents.size() == 1) {
                return Optional.of(contents.get(0));
            }

            // Múltiples contenidos - hacer merge
            List<InputStream> streams = contents.stream()
                    .map(ByteArrayInputStream::new)
                    .map(InputStream.class::cast)
                    .toList();

            return Optional.of(PDFUtils.mergePDFtoByte(streams));

        } catch (Exception e) {
            logger.error("Error generating report for payment: {}", paymentId, e);
            throw new RuntimeException("Failed to generate payment report", e);
        }
    }

    /**
     * Template method - las implementaciones deben generar su contenido específico
     */
    protected abstract List<byte[]> generateReportContents(UUID paymentId);

    /**
     * Método helper para filtrar contenidos vacíos
     */
    protected final List<byte[]> filterValidContents(List<Optional<byte[]>> optionalContents) {
        return optionalContents.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Método helper para crear parámetros comunes
     */
    protected final Map<String, Object> createParameters(String key, Object value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(key, value);
        return parameters;
    }
}
