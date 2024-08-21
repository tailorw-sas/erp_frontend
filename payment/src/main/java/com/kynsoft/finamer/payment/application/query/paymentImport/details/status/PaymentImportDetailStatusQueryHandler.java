package com.kynsoft.finamer.payment.application.query.paymentImport.details.status;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Getter
@Component
public class PaymentImportDetailStatusQueryHandler implements IQueryHandler<PaymentImportDetailStatusQuery,PaymentImportDetailStatusResponse> {

    private final IPaymentImportDetailService paymentImportDetailService;
    @Override
    public PaymentImportDetailStatusResponse handle(PaymentImportDetailStatusQuery query) {
        return paymentImportDetailService.getPaymentImportStatus(query);
    }
}
