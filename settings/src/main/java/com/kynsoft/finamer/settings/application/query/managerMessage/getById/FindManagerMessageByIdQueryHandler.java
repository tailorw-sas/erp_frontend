package com.kynsoft.finamer.settings.application.query.managerMessage.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMessageResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMessageDto;
import com.kynsoft.finamer.settings.domain.services.IManagerMessageService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerMessageByIdQueryHandler implements IQueryHandler<FindManagerMessageByIdQuery, ManagerMessageResponse> {

    private final IManagerMessageService service;

    public FindManagerMessageByIdQueryHandler(IManagerMessageService service) {
        this.service = service;
    }

    @Override
    public ManagerMessageResponse handle(FindManagerMessageByIdQuery query) {
        ManagerMessageDto dto = service.findById(query.getId());

        return new ManagerMessageResponse(dto);
    }
}
