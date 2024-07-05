package com.kynsoft.finamer.payment.application.query.paymentDetail.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentDetailResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentDetailByIdQueryHandler implements IQueryHandler<FindPaymentDetailByIdQuery, PaymentDetailResponse>  {

    private final IPaymentDetailService service;

    public FindPaymentDetailByIdQueryHandler(IPaymentDetailService service) {
        this.service = service;
    }

    @Override
    public PaymentDetailResponse handle(FindPaymentDetailByIdQuery query) {
        PaymentDetailDto response = service.findById(query.getId());

        return new PaymentDetailResponse(response);
    }
}
