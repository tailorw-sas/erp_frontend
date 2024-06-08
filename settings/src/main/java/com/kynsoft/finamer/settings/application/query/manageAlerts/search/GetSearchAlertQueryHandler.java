package com.kynsoft.finamer.settings.application.query.manageAlerts.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IAlertsService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAlertQueryHandler implements IQueryHandler<GetSearchAlertQuery, PaginatedResponse> {
    private final IAlertsService service;
    
    public GetSearchAlertQueryHandler(final IAlertsService service) {this.service=service;}

    @Override
    public PaginatedResponse handle(GetSearchAlertQuery query) {
        return this.service.search(query.getPageable(), query.getFilter());
    }
}
