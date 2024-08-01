package com.kynsoft.finamer.payment.application.query.paymentImport.payment;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportService;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportSatusQueryHandler implements IQueryHandler<PaymentImportStatusQuery,PaymentImportStatusResponse> {

    private final IPaymentImportService paymentImportService;

    public PaymentImportSatusQueryHandler(IPaymentImportService paymentImportService) {
        this.paymentImportService = paymentImportService;
    }

    @Override
    public PaymentImportStatusResponse handle(PaymentImportStatusQuery query) {
        return paymentImportService.getPaymentImportStatus(query);
    }
}
