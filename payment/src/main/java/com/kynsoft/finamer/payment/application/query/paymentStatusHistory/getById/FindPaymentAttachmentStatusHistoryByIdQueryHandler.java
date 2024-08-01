package com.kynsoft.finamer.payment.application.query.paymentStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentStatusHistoryResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import org.springframework.stereotype.Component;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;

@Component
public class FindPaymentAttachmentStatusHistoryByIdQueryHandler implements IQueryHandler<FindPaymentAttachmentStatusHistoryByIdQuery, PaymentStatusHistoryResponse>  {

    private final IPaymentStatusHistoryService service;

    public FindPaymentAttachmentStatusHistoryByIdQueryHandler(IPaymentStatusHistoryService service) {
        this.service = service;
    }

    @Override
    public PaymentStatusHistoryResponse handle(FindPaymentAttachmentStatusHistoryByIdQuery query) {
        PaymentStatusHistoryDto response = service.findById(query.getId());

        return new PaymentStatusHistoryResponse(response);
    }
}
