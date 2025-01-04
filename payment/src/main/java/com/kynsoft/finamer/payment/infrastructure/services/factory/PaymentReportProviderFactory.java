package com.kynsoft.finamer.payment.infrastructure.services.factory;

import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentReportProviderFactory {
    private final Map<String, IPaymentReport> paymentReportServices;

    public PaymentReportProviderFactory(Map<String, IPaymentReport> paymentReportServices) {
        this.paymentReportServices = paymentReportServices;
    }

    public IPaymentReport getPaymentReportService(EPaymentReportType paymentReportType){
        return paymentReportServices.get(paymentReportType.name());
    }
}
