package com.kynsoft.finamer.invoicing.application.query.manageAgencyContact.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.manageAgencyContact.ManageAgencyContactResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyContactDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyContactService;
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
