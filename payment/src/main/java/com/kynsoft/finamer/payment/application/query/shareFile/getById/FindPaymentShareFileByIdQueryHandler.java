package com.kynsoft.finamer.payment.application.query.shareFile.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentStatusHistoryResponse;
import com.kynsoft.finamer.payment.application.query.shareFile.search.PaymentShareFileResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentShareFile;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentShareFileByIdQueryHandler implements IQueryHandler<FindPaymentShareFileByIdQuery, PaymentShareFileResponse>  {

    private final IPaymentShareFileService service;

    public FindPaymentShareFileByIdQueryHandler(IPaymentShareFileService service) {
        this.service = service;
    }

    @Override
    public PaymentShareFileResponse handle(FindPaymentShareFileByIdQuery query) {
        PaymentShareFileDto response = service.findById(query.getId());

        return new PaymentShareFileResponse(response);
    }
}
