package com.kynsoft.finamer.settings.application.query.manageReconcileTransactionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReconcileTransactionStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManageReconcileTransactionStatusService;
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
