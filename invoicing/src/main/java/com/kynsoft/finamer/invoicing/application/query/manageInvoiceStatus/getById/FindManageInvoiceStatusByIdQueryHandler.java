package com.kynsoft.finamer.invoicing.application.query.manageInvoiceStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceStatusResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import org.springframework.stereotype.Component;

@Component
public class FindManageInvoiceStatusByIdQueryHandler implements IQueryHandler<FindManageInvoiceStatusByIdQuery, ManageInvoiceStatusResponse> {

    private final IManageInvoiceStatusService service;

    public FindManageInvoiceStatusByIdQueryHandler(IManageInvoiceStatusService service) {
        this.service = service;
    }

    @Override
    public ManageInvoiceStatusResponse handle(FindManageInvoiceStatusByIdQuery query) {
        ManageInvoiceStatusDto dto = service.findById(query.getId());

        return new ManageInvoiceStatusResponse(dto);
    }
}
