package com.kynsoft.finamer.payment.application.query.paymentImport.details;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.PaymentImportSearchErrorQuery;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailService;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportDetailSearchErrorQueryHandler implements IQueryHandler<PaymentImportDetailSearchErrorQuery,PaymentImportDetailSearchErrorResponse> {

    private final IPaymentImportDetailService paymentImportDetailService;

    public PaymentImportDetailSearchErrorQueryHandler(IPaymentImportDetailService paymentImportDetailService) {
        this.paymentImportDetailService = paymentImportDetailService;
    }

    @Override
    public PaymentImportDetailSearchErrorResponse handle(PaymentImportDetailSearchErrorQuery query) {
        return  new PaymentImportDetailSearchErrorResponse(paymentImportDetailService.getPaymentImportErrors(query));
    }
}
