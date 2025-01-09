package com.kynsoft.finamer.invoicing.application.query.hotelInvoiceNumberSequence.getByTradingCode;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.HotelInvoiceNumberSequenceResponse;
import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import org.springframework.stereotype.Component;

@Component
public class FindHotelInvoiceNumberSequenceByTradingCodeQueryHandler implements IQueryHandler<FindHotelInvoiceNumberSequenceByTradingCodeQuery, HotelInvoiceNumberSequenceResponse>  {

    private final IHotelInvoiceNumberSequenceService service;

    public FindHotelInvoiceNumberSequenceByTradingCodeQueryHandler(IHotelInvoiceNumberSequenceService service) {
        this.service = service;
    }

    @Override
    public HotelInvoiceNumberSequenceResponse handle(FindHotelInvoiceNumberSequenceByTradingCodeQuery query) {
        HotelInvoiceNumberSequenceDto response = service.getByTradingCompanyCodeAndInvoiceType(query.getCode(), query.getInvoiceType());

        return new HotelInvoiceNumberSequenceResponse(response);
    }
}
