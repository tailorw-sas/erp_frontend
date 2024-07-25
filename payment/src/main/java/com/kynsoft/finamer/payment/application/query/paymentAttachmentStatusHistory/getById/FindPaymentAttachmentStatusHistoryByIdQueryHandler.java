package com.kynsoft.finamer.payment.application.query.paymentAttachmentStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentAttachmentStatusHistoryResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentAttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentAttachmentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentAttachmentStatusHistoryByIdQueryHandler implements IQueryHandler<FindPaymentAttachmentStatusHistoryByIdQuery, PaymentAttachmentStatusHistoryResponse>  {

    private final IPaymentAttachmentStatusHistoryService service;

    public FindPaymentAttachmentStatusHistoryByIdQueryHandler(IPaymentAttachmentStatusHistoryService service) {
        this.service = service;
    }

    @Override
    public PaymentAttachmentStatusHistoryResponse handle(FindPaymentAttachmentStatusHistoryByIdQuery query) {
        PaymentAttachmentStatusHistoryDto response = service.findById(query.getId());

        return new PaymentAttachmentStatusHistoryResponse(response);
    }
}
