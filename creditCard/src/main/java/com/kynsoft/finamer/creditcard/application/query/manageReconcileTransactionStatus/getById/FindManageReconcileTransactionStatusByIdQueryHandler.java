package com.kynsoft.finamer.creditcard.application.query.manageReconcileTransactionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageReconcileTransactionStatusResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageReconcileTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class FindManageReconcileTransactionStatusByIdQueryHandler implements IQueryHandler<FindManageReconcileTransactionStatusByIdQuery, ManageReconcileTransactionStatusResponse>  {

    private final IManageReconcileTransactionStatusService service;

    public FindManageReconcileTransactionStatusByIdQueryHandler(IManageReconcileTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public ManageReconcileTransactionStatusResponse handle(FindManageReconcileTransactionStatusByIdQuery query) {
        ManageReconcileTransactionStatusDto response = service.findById(query.getId());

        return new ManageReconcileTransactionStatusResponse(response);
    }
}
