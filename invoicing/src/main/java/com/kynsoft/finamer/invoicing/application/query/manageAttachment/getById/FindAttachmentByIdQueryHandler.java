package com.kynsoft.finamer.invoicing.application.query.manageAttachment.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAttachmentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class FindAttachmentByIdQueryHandler implements IQueryHandler<FindAttachmentByIdQuery, ManageAttachmentResponse>  {

    private final IManageAttachmentService service;

    public FindAttachmentByIdQueryHandler(IManageAttachmentService service) {
        this.service = service;
    }

    @Override
    public ManageAttachmentResponse handle(FindAttachmentByIdQuery query) {
        ManageAttachmentDto response = service.findById(query.getId());

        return new ManageAttachmentResponse(response);
    }
}
