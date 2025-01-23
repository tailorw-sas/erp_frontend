package com.kynsoft.finamer.payment.application.query.payment.getByGenCodeProjection;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentProjectionResponse;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentByGenProjectionQueryHandler implements IQueryHandler<FindPaymentByGenProjectionQuery, PaymentProjectionResponse>  {

    private final IPaymentService service;

    public FindPaymentByGenProjectionQueryHandler(IPaymentService service) {
        this.service = service;
    }

    @Override
    public PaymentProjectionResponse handle(FindPaymentByGenProjectionQuery query) {
        PaymentProjection response = service.findByPaymentIdProjection(query.getId());

        return new PaymentProjectionResponse(response);
    }
}
