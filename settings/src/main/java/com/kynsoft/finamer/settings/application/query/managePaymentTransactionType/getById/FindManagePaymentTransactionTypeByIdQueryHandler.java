package com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManagePaymentTransactionTypeByIdQueryHandler implements IQueryHandler<FindManagePaymentTransactionTypeByIdQuery, ManagePaymentTransactionTypeResponse>  {

    private final IManagePaymentTransactionTypeService service;

    public FindManagePaymentTransactionTypeByIdQueryHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public ManagePaymentTransactionTypeResponse handle(FindManagePaymentTransactionTypeByIdQuery query) {
        ManagePaymentTransactionTypeDto response = service.findById(query.getId());

        return new ManagePaymentTransactionTypeResponse(response);
    }
}
