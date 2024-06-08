package com.kynsoft.finamer.settings.application.query.manageInvoiceTransactionType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageInvoiceTransactionTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageInvoiceTransactionTypeByIdQueryHandler implements IQueryHandler<FindManageInvoiceTransactionTypeByIdQuery, ManageInvoiceTransactionTypeResponse> {

    private final IManageInvoiceTransactionTypeService service;

    public FindManageInvoiceTransactionTypeByIdQueryHandler(IManageInvoiceTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public ManageInvoiceTransactionTypeResponse handle(FindManageInvoiceTransactionTypeByIdQuery query) {
        ManageInvoiceTransactionTypeDto dto = service.findById(query.getId());

        return new ManageInvoiceTransactionTypeResponse(dto);
    }
}
