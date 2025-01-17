package com.kynsoft.finamer.invoicing.application.query.hotelInvoiceNumberSequence.getByCodeHotel;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.HotelInvoiceNumberSequenceResponse;
import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import org.springframework.stereotype.Component;

@Component
public class FindHotelInvoiceNumberSequenceByIdQueryHandler implements IQueryHandler<FindHotelInvoiceNumberSequenceByIdQuery, HotelInvoiceNumberSequenceResponse>  {

    private final IHotelInvoiceNumberSequenceService service;

    public FindHotelInvoiceNumberSequenceByIdQueryHandler(IHotelInvoiceNumberSequenceService service) {
        this.service = service;
    }

    @Override
    public HotelInvoiceNumberSequenceResponse handle(FindHotelInvoiceNumberSequenceByIdQuery query) {
        HotelInvoiceNumberSequenceDto response = service.getByHotelCodeAndInvoiceType(query.getCode(), query.getInvoiceType());

        return new HotelInvoiceNumberSequenceResponse(response);
    }
}
