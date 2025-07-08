package com.kynsoft.finamer.payment.infrastructure.services.factory;

import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class PaymentReportProviderFactory {

    private static final Logger logger = LoggerFactory.getLogger(PaymentReportProviderFactory.class);

    // Mapeo correcto entre enum y bean IDs
    private static final Map<EPaymentReportType, String> BEAN_ID_MAPPING = Map.of(
            EPaymentReportType.PAYMENT_DETAILS, "PAYMENT_DETAILS",
            EPaymentReportType.INVOICE_RELATED, "INVOICE_RELATED",
            EPaymentReportType.INVOICE_RELATED_SUPPORT, "INVOICE_RELATED_SUPPORT",
            EPaymentReportType.PAYMENT_SUPPORT, "PAYMENT_SUPPORT",
            EPaymentReportType.ALL_SUPPORT, "ALL_SUPPORT"
    );

    private final Map<String, IPaymentReport> paymentReportServices;
    private final Map<EPaymentReportType, IPaymentReport> serviceCache;

    public PaymentReportProviderFactory(Map<String, IPaymentReport> paymentReportServices) {
        this.paymentReportServices = paymentReportServices;
        this.serviceCache = new EnumMap<>(EPaymentReportType.class);
        initializeCache();
    }

    public IPaymentReport getPaymentReportService(EPaymentReportType paymentReportType) {
        IPaymentReport service = serviceCache.get(paymentReportType);
        if (service == null) {
            throw new IllegalArgumentException("No payment report service found for type: " + paymentReportType);
        }
        return service;
    }

    public boolean isServiceAvailable(EPaymentReportType reportType) {
        return serviceCache.containsKey(reportType);
    }

    private void initializeCache() {
        for (Map.Entry<EPaymentReportType, String> entry : BEAN_ID_MAPPING.entrySet()) {
            EPaymentReportType reportType = entry.getKey();
            String beanId = entry.getValue();

            IPaymentReport service = paymentReportServices.get(beanId);
            if (service != null) {
                serviceCache.put(reportType, service);
                logger.debug("Registered payment report service: {} -> {}", reportType, beanId);
            } else {
                logger.warn("Payment report service not found for type: {} with bean ID: {}", reportType, beanId);
            }
        }

        logger.info("Initialized PaymentReportProviderFactory with {} services", serviceCache.size());
    }
}
