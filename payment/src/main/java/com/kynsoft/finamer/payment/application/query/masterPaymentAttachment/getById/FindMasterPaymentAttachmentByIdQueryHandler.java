package com.kynsoft.finamer.payment.application.query.masterPaymentAttachment.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.MasterPaymentAttachmentResponse;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class FindMasterPaymentAttachmentByIdQueryHandler implements IQueryHandler<FindMasterPaymentAttachmentByIdQuery, MasterPaymentAttachmentResponse>  {

    private final IMasterPaymentAttachmentService service;

    public FindMasterPaymentAttachmentByIdQueryHandler(IMasterPaymentAttachmentService service) {
        this.service = service;
    }

    @Override
    public MasterPaymentAttachmentResponse handle(FindMasterPaymentAttachmentByIdQuery query) {
        MasterPaymentAttachmentDto response = service.findById(query.getId());

        return new MasterPaymentAttachmentResponse(response);
    }
}
