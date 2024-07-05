package com.kynsoft.finamer.payment.application.query.masterPaymentAttachment.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchMasterPaymentAttachmentQueryHandler implements IQueryHandler<GetSearchMasterPaymentAttachmentQuery, PaginatedResponse> {
    private final IMasterPaymentAttachmentService service;
    
    public GetSearchMasterPaymentAttachmentQueryHandler(IMasterPaymentAttachmentService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchMasterPaymentAttachmentQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
