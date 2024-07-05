package com.kynsoft.finamer.payment.application.query.payment.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentByIdQueryHandler implements IQueryHandler<FindPaymentByIdQuery, PaymentResponse>  {

    private final IPaymentService service;

    public FindPaymentByIdQueryHandler(IPaymentService service) {
        this.service = service;
    }

    @Override
    public PaymentResponse handle(FindPaymentByIdQuery query) {
        PaymentDto response = service.findById(query.getId());

        return new PaymentResponse(response);
    }
}
