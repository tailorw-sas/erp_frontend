package com.kynsoft.finamer.payment.application.query.paymentImport.payment.status;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;


@Component
public class PaymentImportStatusQueryHandler implements IQueryHandler<PaymentImportStatusQuery, PaymentImportStatusResponse> {

    private final IPaymentImportService paymentImportService;

    public PaymentImportStatusQueryHandler(IPaymentImportService paymentImportService) {
        this.paymentImportService = paymentImportService;
    }

    @Override
    public PaymentImportStatusResponse handle(PaymentImportStatusQuery query) {
        return paymentImportService.getPaymentImportStatus(query);
    }
}
