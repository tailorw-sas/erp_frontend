package com.kynsoft.finamer.invoicing.application.query.invoiceStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.InvoiceStatusHistoryResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class FindInvoiceStatusHistoryByIdQueryHandler implements IQueryHandler<FindInvoiceStatusHistoryByIdQuery, InvoiceStatusHistoryResponse>  {

    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;

    public FindInvoiceStatusHistoryByIdQueryHandler(IInvoiceStatusHistoryService invoiceStatusHistoryService) {
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
    }

    @Override
    public InvoiceStatusHistoryResponse handle(FindInvoiceStatusHistoryByIdQuery query) {
        InvoiceStatusHistoryDto response = invoiceStatusHistoryService.findById(query.getId());

        return new InvoiceStatusHistoryResponse(response);
    }
}
