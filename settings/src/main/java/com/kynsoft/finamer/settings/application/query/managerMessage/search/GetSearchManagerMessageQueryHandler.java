package com.kynsoft.finamer.settings.application.query.managerMessage.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerMessageService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerMessageQueryHandler implements IQueryHandler<GetSearchManagerMessageQuery, PaginatedResponse> {

    private final IManagerMessageService service;

    public GetSearchManagerMessageQueryHandler(IManagerMessageService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerMessageQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
