package com.kynsoft.finamer.settings.application.query.manageInvoiceType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageInvoiceTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageInvoiceTypeByIdQueryHandler implements IQueryHandler<FindManageInvoiceTypeByIdQuery, ManageInvoiceTypeResponse> {

    private final IManageInvoiceTypeService service;

    public FindManageInvoiceTypeByIdQueryHandler(IManageInvoiceTypeService service) {
        this.service = service;
    }

    @Override
    public ManageInvoiceTypeResponse handle(FindManageInvoiceTypeByIdQuery query) {
        ManageInvoiceTypeDto dto = service.findById(query.getId());

        return new ManageInvoiceTypeResponse(dto);
    }
}
