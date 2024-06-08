package com.kynsoft.finamer.settings.application.query.manageActionLog.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageActionLogResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageActionLogDto;
import com.kynsoft.finamer.settings.domain.services.IManageActionLogService;
import org.springframework.stereotype.Component;

@Component
public class FindManageActionLogByIdQueryHandler implements IQueryHandler<FindManageActionLogByIdQuery, ManageActionLogResponse> {

    private final IManageActionLogService service;

    public FindManageActionLogByIdQueryHandler(IManageActionLogService service) {
        this.service = service;
    }

    @Override
    public ManageActionLogResponse handle(FindManageActionLogByIdQuery query) {
        ManageActionLogDto dto = service.findById(query.getId());
        return new ManageActionLogResponse(dto);
    }
}
