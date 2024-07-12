package com.kynsoft.finamer.invoicing.application.query.invoicecloseoperation.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.InvoiceCloseOperationResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class FindInvoiceCloseOperationByIdQueryHandler implements IQueryHandler<FindInvoiceCloseOperationByIdQuery, InvoiceCloseOperationResponse>  {

    private final IInvoiceCloseOperationService closeOperationService;

    public FindInvoiceCloseOperationByIdQueryHandler(IInvoiceCloseOperationService closeOperationService) {
        this.closeOperationService = closeOperationService;
    }

    @Override
    public InvoiceCloseOperationResponse handle(FindInvoiceCloseOperationByIdQuery query) {
        InvoiceCloseOperationDto response = closeOperationService.findById(query.getId());

        return new InvoiceCloseOperationResponse(response);
    }
}
