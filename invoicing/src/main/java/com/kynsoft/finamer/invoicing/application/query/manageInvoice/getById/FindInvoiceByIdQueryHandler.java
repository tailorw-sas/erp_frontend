package com.kynsoft.finamer.invoicing.application.query.manageInvoice.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class FindInvoiceByIdQueryHandler implements IQueryHandler<FindInvoiceByIdQuery, ManageInvoiceResponse>  {

    private final IManageInvoiceService service;

    public FindInvoiceByIdQueryHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public ManageInvoiceResponse handle(FindInvoiceByIdQuery query) {
        ManageInvoiceDto response = service.findById(query.getId());

        return new ManageInvoiceResponse(response);
    }
}
