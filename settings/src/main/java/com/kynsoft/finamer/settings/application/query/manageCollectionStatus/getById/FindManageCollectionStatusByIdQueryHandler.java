package com.kynsoft.finamer.settings.application.query.manageCollectionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageCollectionStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageCollectionStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManageCollectionStatusService;
import org.springframework.stereotype.Component;

@Component
public class FindManageCollectionStatusByIdQueryHandler implements IQueryHandler<FindManageCollectionStatusByIdQuery, ManageCollectionStatusResponse> {

    private final IManageCollectionStatusService service;

    public FindManageCollectionStatusByIdQueryHandler(IManageCollectionStatusService service) {
        this.service = service;
    }

    @Override
    public ManageCollectionStatusResponse handle(FindManageCollectionStatusByIdQuery query) {
        ManageCollectionStatusDto dto = service.findById(query.getId());
        return new ManageCollectionStatusResponse(dto);
    }
}
