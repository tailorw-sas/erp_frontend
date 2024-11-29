package com.kynsoft.finamer.creditcard.application.query.hotelPayment.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchHotelPaymentQueryHandler implements IQueryHandler<GetSearchHotelPaymentQuery, PaginatedResponse> {

    private final IHotelPaymentService hotelPaymentService;

    public GetSearchHotelPaymentQueryHandler(IHotelPaymentService hotelPaymentService) {
        this.hotelPaymentService = hotelPaymentService;
    }

    @Override
    public PaginatedResponse handle(GetSearchHotelPaymentQuery query) {
        return this.hotelPaymentService.search(query.getPageable(),query.getFilter());
    }
}
