package com.kynsoft.finamer.payment.application.query.attachmentType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindAttachmentTypeByIdQueryHandler implements IQueryHandler<FindAttachmentTypeByIdQuery, AttachmentTypeResponse>  {

    private final IManageAttachmentTypeService service;

    public FindAttachmentTypeByIdQueryHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public AttachmentTypeResponse handle(FindAttachmentTypeByIdQuery query) {
        AttachmentTypeDto response = service.findById(query.getId());

        return new AttachmentTypeResponse(response);
    }
}
