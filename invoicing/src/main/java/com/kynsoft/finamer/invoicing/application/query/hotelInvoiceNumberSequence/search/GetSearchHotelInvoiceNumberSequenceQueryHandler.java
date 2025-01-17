package com.kynsoft.finamer.invoicing.application.query.hotelInvoiceNumberSequence.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchHotelInvoiceNumberSequenceQueryHandler implements IQueryHandler<GetSearchHotelInvoiceNumberSequenceQuery, PaginatedResponse> {
    private final IHotelInvoiceNumberSequenceService service;
    
    public GetSearchHotelInvoiceNumberSequenceQueryHandler(IHotelInvoiceNumberSequenceService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchHotelInvoiceNumberSequenceQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
