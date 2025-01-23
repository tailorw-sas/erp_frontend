package com.kynsoft.finamer.payment.application.query.managePaymentTransactionType.getByCode;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManagePaymentTransactionTypeByCodeQueryHandler implements IQueryHandler<FindManagePaymentTransactionTypeByCodeQuery, ManagePaymentTransactionTypeResponse>  {

    private final IManagePaymentTransactionTypeService service;

    public FindManagePaymentTransactionTypeByCodeQueryHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public ManagePaymentTransactionTypeResponse handle(FindManagePaymentTransactionTypeByCodeQuery query) {
        ManagePaymentTransactionTypeDto response = service.findByCode(query.getCode());

        return new ManagePaymentTransactionTypeResponse(response);
    }
}
