package com.kynsoft.finamer.payment.application.query.payment.countByAgency;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class CountAgencyByPaymentBalanceAndDepositBalanceHandler implements IQueryHandler<CountAgencyByPaymentBalanceAndDepositBalanceQuery, CountAgencyByPaymentBalanceAndDepositBalanceResponse>  {

    private final IPaymentService service;

    public CountAgencyByPaymentBalanceAndDepositBalanceHandler(IPaymentService service) {
        this.service = service;
    }

    @Override
    public CountAgencyByPaymentBalanceAndDepositBalanceResponse handle(CountAgencyByPaymentBalanceAndDepositBalanceQuery query) {
        Long cant = service.countByAgency(query.getAgencyId());
        boolean inactive = !(cant > 0);//Si devuelve falso es que no se puede desactiva o sea que existe Angencia en algun payment con paymentBalance o depositBalance Mayor que cero.

        return new CountAgencyByPaymentBalanceAndDepositBalanceResponse(inactive);
    }
}
