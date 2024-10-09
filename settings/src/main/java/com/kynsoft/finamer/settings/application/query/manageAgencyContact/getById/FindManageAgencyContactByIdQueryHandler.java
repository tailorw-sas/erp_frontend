package com.kynsoft.finamer.settings.application.query.manageAgencyContact.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.manageAgencyContact.ManageAgencyContactResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyContactDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyContactService;
import org.springframework.stereotype.Component;

@Component
public class FindManageAgencyContactByIdQueryHandler implements IQueryHandler<FindManageAgencyContactByIdQuery, ManageAgencyContactResponse> {

    private final IManageAgencyContactService service;

    public FindManageAgencyContactByIdQueryHandler(IManageAgencyContactService service) {
        this.service = service;
    }

    @Override
    public ManageAgencyContactResponse handle(FindManageAgencyContactByIdQuery query) {
        ManageAgencyContactDto dto = service.findById(query.getId());
        return new ManageAgencyContactResponse(dto);
    }
}
