package com.kynsoft.finamer.creditcard.application.query.managePaymentTransactionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManagePaymentTransactionStatusResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagePaymentTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class FindManagePaymentTransactionStatusByIdQueryHandler implements IQueryHandler<FindManagePaymentTransactionStatusByIdQuery, ManagePaymentTransactionStatusResponse> {

    private final IManagePaymentTransactionStatusService service;

    public FindManagePaymentTransactionStatusByIdQueryHandler(IManagePaymentTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public ManagePaymentTransactionStatusResponse handle(FindManagePaymentTransactionStatusByIdQuery query) {
        ManagePaymentTransactionStatusDto dto = this.service.findById(query.getId());
        return new ManagePaymentTransactionStatusResponse(dto);
    }
}
