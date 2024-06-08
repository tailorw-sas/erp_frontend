package com.kynsoft.finamer.settings.application.query.managePaymentStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerPaymentStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerPaymentStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentStatusByIdQueryHandler implements IQueryHandler<FindPaymentStatusByIdQuery, ManagerPaymentStatusResponse> {
    private final IManagerPaymentStatusService service;
    
    public FindPaymentStatusByIdQueryHandler(final IManagerPaymentStatusService service) {
        this.service = service;
    }
    
    @Override
    public ManagerPaymentStatusResponse handle(FindPaymentStatusByIdQuery query) {
        ManagerPaymentStatusDto dto = service.findById(query.getId());
        
        return new ManagerPaymentStatusResponse(dto);
    }
}
