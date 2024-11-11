package com.kynsoft.finamer.creditcard.application.query.manageVCCTransactionType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageVCCTransactionTypeResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageVCCTransactionTypeByIdQueryHandler implements IQueryHandler<FindManageVCCTransactionTypeByIdQuery, ManageVCCTransactionTypeResponse>  {

    private final IManageVCCTransactionTypeService service;

    public FindManageVCCTransactionTypeByIdQueryHandler(IManageVCCTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public ManageVCCTransactionTypeResponse handle(FindManageVCCTransactionTypeByIdQuery query) {
        ManageVCCTransactionTypeDto response = service.findById(query.getId());

        return new ManageVCCTransactionTypeResponse(response);
    }
}
