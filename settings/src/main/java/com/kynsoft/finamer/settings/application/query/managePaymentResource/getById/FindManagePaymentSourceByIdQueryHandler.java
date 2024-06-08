package com.kynsoft.finamer.settings.application.query.managePaymentResource.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentSourceResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentSourceService;
import org.springframework.stereotype.Component;

@Component
public class FindManagePaymentSourceByIdQueryHandler implements IQueryHandler<FindManagePaymentSourceByIdQuery, ManagePaymentSourceResponse> {

    private final IManagePaymentSourceService service;

    public FindManagePaymentSourceByIdQueryHandler(IManagePaymentSourceService service) {
        this.service = service;
    }

    @Override
    public ManagePaymentSourceResponse handle(FindManagePaymentSourceByIdQuery query) {
        ManagePaymentSourceDto dto = service.findById(query.getId());

        return new ManagePaymentSourceResponse(dto);
    }
}
