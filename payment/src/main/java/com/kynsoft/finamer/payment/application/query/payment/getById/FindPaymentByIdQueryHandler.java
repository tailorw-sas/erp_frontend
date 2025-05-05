package com.kynsoft.finamer.payment.application.query.payment.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FindPaymentByIdQueryHandler implements IQueryHandler<FindPaymentByIdQuery, PaymentResponse>  {

    private final IPaymentService service;
    private final IPaymentDetailService paymentDetailService;

    public FindPaymentByIdQueryHandler(IPaymentService service,
                                       IPaymentDetailService paymentDetailService) {
        this.service = service;
        this.paymentDetailService = paymentDetailService;
    }

    @Override
    public PaymentResponse handle(FindPaymentByIdQuery query) {
        PaymentDto response = service.findByIdCustom(query.getId());
        response.setPaymentDetails(getDetailsByPaymentId(response.getId()));
        return new PaymentResponse(response);
    }

    private List<PaymentDetailDto> getDetailsByPaymentId(UUID id){
        if(Objects.isNull(id)){
            return Collections.emptyList();
        }

        return paymentDetailService.findByPaymentId(id);
    }
}
