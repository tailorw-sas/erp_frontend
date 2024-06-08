package com.kynsoft.finamer.settings.application.query.manageAlerts.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAlertsResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAlertsDto;
import com.kynsoft.finamer.settings.domain.services.IAlertsService;
import org.springframework.stereotype.Component;

@Component
public class FindAlertByIdQueryHandler implements IQueryHandler<FindAlertByIdQuery, ManageAlertsResponse> {
    
    private final IAlertsService service;
    
    public FindAlertByIdQueryHandler(final IAlertsService service) {this.service = service;}
    
    @Override
    public ManageAlertsResponse handle(FindAlertByIdQuery query) {
        ManageAlertsDto response = service.findById(query.getId());
        
        return new ManageAlertsResponse(response);
    }
}
