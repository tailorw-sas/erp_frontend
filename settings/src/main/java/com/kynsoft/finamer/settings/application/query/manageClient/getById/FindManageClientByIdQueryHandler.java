package com.kynsoft.finamer.settings.application.query.manageClient.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageClientResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageClientDto;
import com.kynsoft.finamer.settings.domain.services.IManagerClientService;
import org.springframework.stereotype.Component;

@Component
public class FindManageClientByIdQueryHandler implements IQueryHandler<FindManageClientByIdQuery, ManageClientResponse> {

    private final IManagerClientService service;

    public FindManageClientByIdQueryHandler(IManagerClientService service) {
        this.service = service;
    }

    @Override
    public ManageClientResponse handle(FindManageClientByIdQuery query) {
        ManageClientDto dto = service.findById(query.getId());

        return new ManageClientResponse(dto);
    }
}
