package com.kynsoft.finamer.creditcard.application.query.manageTransactionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageTransactionStatusResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class FindManageTransactionStatusByIdQueryHandler implements IQueryHandler<FindManageTransactionStatusByIdQuery, ManageTransactionStatusResponse>  {

    private final IManageTransactionStatusService service;

    public FindManageTransactionStatusByIdQueryHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public ManageTransactionStatusResponse handle(FindManageTransactionStatusByIdQuery query) {
        ManageTransactionStatusDto response = service.findById(query.getId());

        return new ManageTransactionStatusResponse(response);
    }
}
