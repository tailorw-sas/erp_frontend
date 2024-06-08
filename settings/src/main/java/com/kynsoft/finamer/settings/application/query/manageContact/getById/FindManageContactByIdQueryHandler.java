package com.kynsoft.finamer.settings.application.query.manageContact.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageContactResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageContactDto;
import com.kynsoft.finamer.settings.domain.services.IManageContactService;
import org.springframework.stereotype.Component;

@Component
public class FindManageContactByIdQueryHandler implements IQueryHandler<FindManageContactByIdQuery, ManageContactResponse> {

    private final IManageContactService service;

    public FindManageContactByIdQueryHandler(IManageContactService service) {
        this.service = service;
    }

    @Override
    public ManageContactResponse handle(FindManageContactByIdQuery query) {
        ManageContactDto dto = service.findById(query.getId());

        return new ManageContactResponse(dto);
    }
}
