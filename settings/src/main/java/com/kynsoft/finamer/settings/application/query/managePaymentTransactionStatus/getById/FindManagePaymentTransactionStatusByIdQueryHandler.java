package com.kynsoft.finamer.settings.application.query.managePaymentTransactionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentTransactionStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionStatusService;

import org.springframework.stereotype.Component;

@Component
public class FindManagePaymentTransactionStatusByIdQueryHandler implements IQueryHandler<FindManagePaymentTransactionStatusByIdQuery, ManagePaymentTransactionStatusResponse>  {

    private final IManagePaymentTransactionStatusService service;

    public FindManagePaymentTransactionStatusByIdQueryHandler(IManagePaymentTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public ManagePaymentTransactionStatusResponse handle(FindManagePaymentTransactionStatusByIdQuery query) {
        ManagePaymentTransactionStatusDto response = service.findById(query.getId());

        return new ManagePaymentTransactionStatusResponse(response);
    }
}
