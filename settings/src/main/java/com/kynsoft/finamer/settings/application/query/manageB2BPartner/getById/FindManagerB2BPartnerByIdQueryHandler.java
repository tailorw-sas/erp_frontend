package com.kynsoft.finamer.settings.application.query.manageB2BPartner.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerB2BPartnerResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.settings.domain.services.IManagerB2BPartnerService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerB2BPartnerByIdQueryHandler implements IQueryHandler<FindManagerB2BPartnerByIdQuery, ManagerB2BPartnerResponse>  {

    private final IManagerB2BPartnerService service;

    public FindManagerB2BPartnerByIdQueryHandler(IManagerB2BPartnerService service) {
        this.service = service;
    }

    @Override
    public ManagerB2BPartnerResponse handle(FindManagerB2BPartnerByIdQuery query) {
        ManagerB2BPartnerDto response = service.findById(query.getId());

        return new ManagerB2BPartnerResponse(response);
    }
}
