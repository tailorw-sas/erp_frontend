package com.kynsoft.finamer.payment.application.query.paymentImport.payment.error;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportService;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportSearchErrorQueryHandler implements IQueryHandler<PaymentImportSearchErrorQuery,PaymentImportSearchErrorResponse> {

    private final IPaymentImportService paymentImportService;

    public PaymentImportSearchErrorQueryHandler(IPaymentImportService paymentImportService) {
        this.paymentImportService = paymentImportService;
    }

    @Override
    public PaymentImportSearchErrorResponse handle(PaymentImportSearchErrorQuery query) {
        return new PaymentImportSearchErrorResponse(paymentImportService.getPaymentImportErrors(query));
    }
}
