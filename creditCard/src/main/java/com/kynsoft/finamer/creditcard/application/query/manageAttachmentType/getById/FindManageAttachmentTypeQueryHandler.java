package com.kynsoft.finamer.creditcard.application.query.manageAttachmentType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageAttachmentTypeResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageAttachmentTypeQueryHandler implements IQueryHandler<FindManageAttachmentTypeByIdQuery, ManageAttachmentTypeResponse> {

    private final IManageAttachmentTypeService service;

    public FindManageAttachmentTypeQueryHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public ManageAttachmentTypeResponse handle(FindManageAttachmentTypeByIdQuery query) {
        ManageAttachmentTypeDto dto = service.findById(query.getId());
        return new ManageAttachmentTypeResponse(dto);
    }
}
