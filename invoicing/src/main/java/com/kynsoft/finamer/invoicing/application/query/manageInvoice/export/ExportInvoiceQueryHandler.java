package com.kynsoft.finamer.invoicing.application.query.manageInvoice.export;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ExportInvoiceResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class ExportInvoiceQueryHandler implements IQueryHandler<ExportInvoiceQuery, ExportInvoiceResponse> {
    private final IManageInvoiceService service;
    
    public ExportInvoiceQueryHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public ExportInvoiceResponse handle(ExportInvoiceQuery query) {

        ExportInvoiceResponse response = new ExportInvoiceResponse();

         this.service.exportInvoiceList(query.getPageable(),query.getFilter(),response.getStream());

         return  response;
    }
}
