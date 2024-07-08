package com.kynsoft.finamer.payment.application.query.paymentcloseoperation.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentCloseOperationResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentCloseOperationByIdQueryHandler implements IQueryHandler<FindPaymentCloseOperationByIdQuery, PaymentCloseOperationResponse>  {

    private final IPaymentCloseOperationService service;

    public FindPaymentCloseOperationByIdQueryHandler(IPaymentCloseOperationService service) {
        this.service = service;
    }

    @Override
    public PaymentCloseOperationResponse handle(FindPaymentCloseOperationByIdQuery query) {
        PaymentCloseOperationDto response = service.findById(query.getId());

        return new PaymentCloseOperationResponse(response);
    }
}
