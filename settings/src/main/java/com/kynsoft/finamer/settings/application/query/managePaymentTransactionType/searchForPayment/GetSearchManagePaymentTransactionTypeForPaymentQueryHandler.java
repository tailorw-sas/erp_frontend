package com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.searchForPayment;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagePaymentTransactionTypeForPaymentQueryHandler implements IQueryHandler<GetManagePaymentTransactionTypeForPaymentQuery, PaginatedResponse>{
    private final IManagePaymentTransactionTypeService service;

    public GetSearchManagePaymentTransactionTypeForPaymentQueryHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManagePaymentTransactionTypeForPaymentQuery query) {

        return this.service.findWithApplyDepositAndDepositFalse(query.getApplyDeposit(), query.getDeposit(), query.getDefaults(), query.getPageable());
    }
}
