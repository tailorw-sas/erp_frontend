package com.kynsoft.finamer.creditcard.application.query.attachment.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.AttachmentResponse;
import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class FindAttachmentByIdQueryHandler implements IQueryHandler<FindAttachmentByIdQuery, AttachmentResponse>  {

    private final IAttachmentService service;

    public FindAttachmentByIdQueryHandler(IAttachmentService service) {
        this.service = service;
    }

    @Override
    public AttachmentResponse handle(FindAttachmentByIdQuery query) {
        AttachmentDto response = service.findById(query.getId());

        return new AttachmentResponse(response);
    }
}
