package com.kynsoft.finamer.payment.application.query.paymentDetail.getByIdInWrite;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentDetailResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentDetailByIdInWriteQueryHandler implements IQueryHandler<FindPaymentDetailByIdInWriteQuery, PaymentDetailResponse>  {

    private final IPaymentDetailService service;

    public FindPaymentDetailByIdInWriteQueryHandler(IPaymentDetailService service) {
        this.service = service;
    }

    @Override
    public PaymentDetailResponse handle(FindPaymentDetailByIdInWriteQuery query) {
        PaymentDetailDto response = service.findByIdInWrite(query.getId());

        return new PaymentDetailResponse(response);
    }
}
